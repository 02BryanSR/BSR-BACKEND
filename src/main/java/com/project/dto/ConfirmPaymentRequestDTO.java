package com.project.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfirmPaymentRequestDTO {

    @NotBlank(message = "paymentIntentId is required")
    private String paymentIntentId;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
}
