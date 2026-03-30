package com.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.*;

import com.project.dto.ProductAdminForm;
import com.project.dto.ProductDTO;
import com.project.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> findByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(service.findByCategoryId(categoryId));
    }

    @GetMapping("/name")
    public ResponseEntity<List<ProductDTO>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> getProductStock(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProductStock(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> create(@Valid @ModelAttribute ProductAdminForm form) {
        ProductDTO created = service.createProduct(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @ModelAttribute ProductAdminForm form) {
        return ResponseEntity.ok(service.updateProduct(id, form));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock/increase")
    public ResponseEntity<ProductDTO> increaseStock(@PathVariable Long id, @RequestParam Integer amount) {
        return ResponseEntity.ok(service.increaseStock(id, amount));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock/decrease")
    public ResponseEntity<ProductDTO> decreaseStock(@PathVariable Long id, @RequestParam Integer amount) {
        return ResponseEntity.ok(service.decreaseStock(id, amount));
    }
}
