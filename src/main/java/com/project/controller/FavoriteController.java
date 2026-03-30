package com.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.FavoriteDTO;
import com.project.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService service;

    public FavoriteController(FavoriteService service) {
        this.service = service;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<List<FavoriteDTO>> myFavorites(Authentication auth) {
        return ResponseEntity.ok(service.findMyFavorites(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/{productId}/exists")
    public ResponseEntity<Boolean> isMyFavorite(@PathVariable Long productId, Authentication auth) {
        return ResponseEntity.ok(service.isMyFavorite(productId, auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me")
    public ResponseEntity<FavoriteDTO> addMyFavorite(@RequestParam Long productId, Authentication auth) {
        FavoriteDTO created = service.addMyFavorite(productId, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me/{productId}")
    public ResponseEntity<Void> deleteMyFavorite(@PathVariable Long productId, Authentication auth) {
        service.deleteMyFavorite(productId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
