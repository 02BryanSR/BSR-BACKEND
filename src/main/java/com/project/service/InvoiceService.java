package com.project.service;

import com.project.entity.OrderEntity;

public interface InvoiceService {
    byte[] generateInvoicePdf(OrderEntity order);

    String buildInvoiceFilename(Long orderId);
}
