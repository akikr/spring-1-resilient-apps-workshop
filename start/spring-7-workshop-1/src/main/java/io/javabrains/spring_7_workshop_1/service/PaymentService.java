package io.javabrains.spring_7_workshop_1.service;

import io.javabrains.spring_7_workshop_1.exception.PaymentNetworkException;
import io.javabrains.spring_7_workshop_1.exception.PaymentValidationException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {

    private final Random random = new Random();

    public String processPayment(String orderId, double amount) {
        if (amount <= 0) {
            throw new PaymentValidationException("Payment amount must be positive. Received: " + amount);
        }

        if (random.nextBoolean()) {
            throw new PaymentNetworkException("Network error: Unable to connect to payment gateway");
        }

        return "Payment of $" + amount + " processed successfully for order: " + orderId;
    }
}
