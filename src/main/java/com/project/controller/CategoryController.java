package com.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.*;

import com.project.dto.CategoryForm;
import com.project.dto.CategoryDTO;
import com.project.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryDTO> create(@Valid @ModelAttribute CategoryForm form) {
        CategoryDTO created = service.createCategory(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryDTO> update(@Valid @ModelAttribute CategoryForm form, @PathVariable Long id) {
        CategoryDTO updated = service.updateCategory(form, id);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
