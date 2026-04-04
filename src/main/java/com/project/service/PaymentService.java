package com.project.service;

import com.project.dto.ConfirmPaymentRequestDTO;
import com.project.dto.CreatePaymentIntentRequestDTO;
import com.project.dto.OrderDTO;
import com.project.dto.PaymentIntentResponseDTO;

public interface PaymentService {

    PaymentIntentResponseDTO createPaymentIntent(CreatePaymentIntentRequestDTO dto, String email);

    OrderDTO confirmPayment(ConfirmPaymentRequestDTO dto, String email);

    void handleWebhook(String payload, String signatureHeader);
}
