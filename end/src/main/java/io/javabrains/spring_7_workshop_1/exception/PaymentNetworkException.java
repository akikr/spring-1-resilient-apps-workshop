package io.javabrains.spring_7_workshop_1.exception;

public class PaymentNetworkException extends RuntimeException {

    public PaymentNetworkException(String message) {
        super(message);
    }
}