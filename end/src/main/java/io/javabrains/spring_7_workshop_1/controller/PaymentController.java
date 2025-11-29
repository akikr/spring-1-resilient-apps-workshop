package io.javabrains.spring_7_workshop_1.controller;

import io.javabrains.spring_7_workshop_1.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/pay")
    public String pay(@RequestParam String orderId, @RequestParam double amount) {
        return paymentService.processPayment(orderId, amount);
    }
}
