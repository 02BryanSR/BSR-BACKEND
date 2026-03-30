package com.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.dto.OrderDetailDTO;
import com.project.service.OrderDetailService;

@RestController
@RequestMapping("/api/orders/{orderId}/details")
public class OrderDetailController {

    private final OrderDetailService service;

    public OrderDetailController(OrderDetailService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<OrderDetailDTO>> findByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.findByOrderId(orderId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<OrderDetailDTO>> findMyOrderDetails(@PathVariable Long orderId,
                                                                   Authentication auth) {
        return ResponseEntity.ok(service.findMyOrderDetails(orderId, auth.getName()));
    }
}