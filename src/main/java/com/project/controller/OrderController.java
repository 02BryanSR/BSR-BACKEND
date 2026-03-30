package com.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.dto.CreateOrderRequestDTO;
import com.project.dto.OrderDTO;
import com.project.entity.enums.OrderStatus;
import com.project.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> findByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.findByCustomerId(customerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> findByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<List<OrderDTO>> myOrders(Authentication auth) {
        return ResponseEntity.ok(service.findMyOrders(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/{id}")
    public ResponseEntity<OrderDTO> myOrderById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(service.findMyOrderById(id, auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me")
    public ResponseEntity<OrderDTO> createMyOrder(@Valid @RequestBody CreateOrderRequestDTO dto, Authentication auth) {
        OrderDTO created = service.createMyOrder(dto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


}