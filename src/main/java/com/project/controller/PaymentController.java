package com.project.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.ConfirmPaymentRequestDTO;
import com.project.dto.CreatePaymentIntentRequestDTO;
import com.project.dto.OrderDTO;
import com.project.dto.PaymentIntentResponseDTO;
import com.project.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/intent")
    public ResponseEntity<PaymentIntentResponseDTO> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequestDTO dto,
            Authentication authentication) {
        PaymentIntentResponseDTO response = paymentService.createPaymentIntent(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/confirm")
    public ResponseEntity<OrderDTO> confirmPayment(
            @Valid @RequestBody ConfirmPaymentRequestDTO dto,
            Authentication authentication) {
        OrderDTO response = paymentService.confirmPayment(dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String signatureHeader) {
        paymentService.handleWebhook(payload, signatureHeader);
        return ResponseEntity.ok(Map.of("status", "received"));
    }
}
