package io.javabrains.spring_7_workshop_1;

import io.javabrains.spring_7_workshop_1.exception.PaymentNetworkException;
import io.javabrains.spring_7_workshop_1.exception.PaymentValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PaymentServiceRetryTest {

    @Autowired
    private TestablePaymentService testablePaymentService;

    @TestConfiguration
    @EnableResilientMethods
    static class TestConfig {

        @Bean
        @Primary
        public TestablePaymentService testablePaymentService() {
            return new TestablePaymentService();
        }
    }

    /**
     * A testable version of PaymentService that allows us to control
     * when network exceptions are thrown and count invocations.
     */
    @Service
    static class TestablePaymentService {

        private final AtomicInteger invocationCount = new AtomicInteger(0);
        private int failuresBeforeSuccess = 0;

        public void reset() {
            invocationCount.set(0);
            failuresBeforeSuccess = 0;
        }

        public void setFailuresBeforeSuccess(int failures) {
            this.failuresBeforeSuccess = failures;
        }

        public int getInvocationCount() {
            return invocationCount.get();
        }

        @Retryable(
            maxRetries = 3,
            delay = 100,  // Shorter delay for tests
            multiplier = 2.0,
            jitter = 10,
            includes = {PaymentNetworkException.class},
            excludes = {PaymentValidationException.class}
        )
        public String processPayment(String orderId, double amount) {
            int currentInvocation = invocationCount.incrementAndGet();

            if (amount <= 0) {
                throw new PaymentValidationException("Payment amount must be positive. Received: " + amount);
            }

            if (currentInvocation <= failuresBeforeSuccess) {
                throw new PaymentNetworkException("Network error: Unable to connect to payment gateway");
            }

            return "Payment of $" + amount + " processed successfully for order: " + orderId;
        }
    }

    @Test
    void shouldSucceedOnFirstAttempt() {
        testablePaymentService.reset();
        testablePaymentService.setFailuresBeforeSuccess(0);

        String result = testablePaymentService.processPayment("order-123", 50.00);

        assertThat(result).contains("successfully");
        assertThat(testablePaymentService.getInvocationCount()).isEqualTo(1);
    }

    @Test
    void shouldRetryAndSucceedOnSecondAttempt() {
        testablePaymentService.reset();
        testablePaymentService.setFailuresBeforeSuccess(1);

        String result = testablePaymentService.processPayment("order-123", 50.00);

        assertThat(result).contains("successfully");
        assertThat(testablePaymentService.getInvocationCount()).isEqualTo(2);
    }

    @Test
    void shouldRetryAndSucceedOnThirdAttempt() {
        testablePaymentService.reset();
        testablePaymentService.setFailuresBeforeSuccess(2);

        String result = testablePaymentService.processPayment("order-123", 50.00);

        assertThat(result).contains("successfully");
        assertThat(testablePaymentService.getInvocationCount()).isEqualTo(3);
    }

    @Test
    void shouldExhaustRetriesAndThrowException() {
        testablePaymentService.reset();
        testablePaymentService.setFailuresBeforeSuccess(10);  // More than maxRetries

        assertThatThrownBy(() -> testablePaymentService.processPayment("order-123", 50.00))
            .isInstanceOf(PaymentNetworkException.class);

        // 1 initial + 3 retries = 4 total attempts
        assertThat(testablePaymentService.getInvocationCount()).isEqualTo(4);
    }

    @Test
    void shouldNotRetryValidationException() {
        testablePaymentService.reset();

        assertThatThrownBy(() -> testablePaymentService.processPayment("order-123", -10.00))
            .isInstanceOf(PaymentValidationException.class);

        // Should only be called once - no retries for validation exceptions
        assertThat(testablePaymentService.getInvocationCount()).isEqualTo(1);
    }

    @Test
    void shouldRespectExponentialBackoff() {
        testablePaymentService.reset();
        testablePaymentService.setFailuresBeforeSuccess(10);

        long startTime = System.currentTimeMillis();

        assertThatThrownBy(() -> testablePaymentService.processPayment("order-123", 50.00))
            .isInstanceOf(PaymentNetworkException.class);

        long duration = System.currentTimeMillis() - startTime;

        // With delay=100, multiplier=2.0, jitter=10:
        // Wait times: ~100ms, ~200ms, ~400ms = ~700ms total (with some jitter variance)
        // Using generous bounds to account for jitter and system variance
        assertThat(duration).isGreaterThanOrEqualTo(600);
        assertThat(duration).isLessThan(1500);
    }
}
