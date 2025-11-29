package io.javabrains.spring_7_workshop_1.exception;

public class PaymentValidationException extends RuntimeException {

    public PaymentValidationException(String message) {
        super(message);
    }
}
