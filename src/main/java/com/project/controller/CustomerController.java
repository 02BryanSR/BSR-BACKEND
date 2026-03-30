package com.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.dto.CustomerCreateDTO;
import com.project.dto.CustomerDTO;
import com.project.dto.CustomerUpdateDTO;
import com.project.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<List<CustomerDTO>> findByLastNames(@PathVariable String lastName) {
        return ResponseEntity.ok(service.findByLastName(lastName));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email")
    public ResponseEntity<CustomerDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.findByEmailIgnoreCase(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerCreateDTO dto) {
        CustomerDTO created = service.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@Valid @RequestBody CustomerUpdateDTO dto,
                                              @PathVariable Long id,
                                              Authentication auth) {
        CustomerDTO updated = service.updateCustomer(dto, id, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.deleteCustomer(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<CustomerDTO> myProfile(Authentication auth) {
        return ResponseEntity.ok(service.findByEmailIgnoreCase(auth.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me")
    public ResponseEntity<CustomerDTO> updateMyProfile(@Valid @RequestBody CustomerUpdateDTO dto,
                                                       Authentication auth) {
        return ResponseEntity.ok(service.updateMyProfile(dto, auth.getName()));
    }
}
