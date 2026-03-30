package com.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.dto.CartResponseDTO;
import com.project.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<CartResponseDTO> getMyCart(Authentication auth) {
        return ResponseEntity.ok(service.getMyCart(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/items")
    public ResponseEntity<CartResponseDTO> addItemToMyCart(
            Authentication auth,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        service.addItemToMyCart(auth.getName(), productId, quantity);
        return ResponseEntity.ok(service.getMyCart(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateMyItemQuantity(
            Authentication auth,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        service.updateMyItemQuantity(auth.getName(), productId, quantity);
        return ResponseEntity.ok(service.getMyCart(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeMyItem(
            Authentication auth,
            @PathVariable Long productId) {
        service.removeMyItem(auth.getName(), productId);
        return ResponseEntity.ok(service.getMyCart(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<CartResponseDTO> clearMyCart(Authentication auth) {
        service.clearMyCart(auth.getName());
        return ResponseEntity.ok(service.getMyCart(auth.getName()));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponseDTO> getCartByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getCart(customerId));
    }
}