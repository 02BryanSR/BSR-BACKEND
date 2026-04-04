package com.project.service;

import com.project.entity.OrderEntity;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink);

    void sendOrderConfirmationEmail(OrderEntity order);
}
