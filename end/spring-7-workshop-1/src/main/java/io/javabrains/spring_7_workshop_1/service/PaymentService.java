package io.javabrains.spring_7_workshop_1.service;

import io.javabrains.spring_7_workshop_1.exception.PaymentNetworkException;
import io.javabrains.spring_7_workshop_1.exception.PaymentValidationException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentService {

    private final Random random = new Random();
    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    @Retryable(
        maxRetries = 3,
        delay = 1000,
        multiplier = 2.0,
        jitter = 100,
        includes = {PaymentNetworkException.class},
        excludes = {PaymentValidationException.class}
    )
    public String processPayment(String orderId, double amount) {
        int attempt = attemptCounter.incrementAndGet();
        System.out.println(">>> Attempt #" + attempt + " - Processing payment for order: " + orderId + ", amount: $" + amount);

        if (amount <= 0) {
            System.out.println(">>> Validation failed - negative amount. No retry will occur.");
            attemptCounter.set(0);
            throw new PaymentValidationException("Payment amount must be positive. Received: " + amount);
        }

        if (random.nextBoolean()) {
            System.out.println(">>> Attempt #" + attempt + " FAILED - Network error! Will retry if attempts remain...");
            throw new PaymentNetworkException("Network error: Unable to connect to payment gateway");
        }

        System.out.println(">>> Attempt #" + attempt + " SUCCEEDED - Payment processed!");
        attemptCounter.set(0);
        return "Payment of $" + amount + " processed successfully for order: " + orderId;
    }
}
