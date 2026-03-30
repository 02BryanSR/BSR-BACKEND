package com.project.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.dto.CustomerCreateDTO;
import com.project.dto.CustomerDTO;
import com.project.dto.CustomerUpdateDTO;
import com.project.entity.CustomerEntity;
import com.project.entity.enums.Role;
import com.project.mapper.CustomerMapper;
import com.project.repository.CustomerRepository;
import com.project.service.CustomerService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper mapper;
    private final CustomerRepository repo;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerMapper mapper,
                               CustomerRepository repo,
                               PasswordEncoder passwordEncoder) {
        this.mapper = mapper;
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<CustomerDTO> findAll() {
        return mapper.toListDtos(repo.findAll());
    }

    @Override
    public List<CustomerDTO> findByLastName(String lastName) {
        return mapper.toListDtos(repo.findByLastNameContaining(lastName));
    }

    @Override
    public CustomerDTO findById(Long id) {
        CustomerEntity customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapper.toDto(customer);
    }

    @Override
    public CustomerDTO findByEmailIgnoreCase(String email) {
        CustomerEntity customer = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapper.toDto(customer);
    }

    @Override
    public CustomerDTO createCustomer(CustomerCreateDTO dto) {
        validateCreate(dto);

        String email = normalizeEmail(dto.getEmail());

        if (repo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setName(dto.getName().trim());
        customer.setLastName(dto.getLastName().trim());
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        customer.setNumber(dto.getNumber());
        customer.setRole(parseRole(dto.getRole()));
        customer.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);

        CustomerEntity saved = repo.save(customer);
        return mapper.toDto(saved);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerUpdateDTO dto, Long id, String currentEmail) {
        CustomerEntity customerFound = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerEntity currentAdmin = repo.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("Current admin not found"));

        boolean isSelf = currentAdmin.getId().equals(customerFound.getId());

        if (isSelf && dto.getEnabled() != null && !dto.getEnabled()) {
            throw new AccessDeniedException("You cannot deactivate your own account");
        }

        if (isSelf && dto.getRole() != null && !dto.getRole().isBlank()) {
            Role nextRole = parseRole(dto.getRole());
            if (nextRole != Role.ADMIN) {
                throw new AccessDeniedException("You cannot remove your own admin role");
            }
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            customerFound.setName(dto.getName().trim());
        }

        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            customerFound.setLastName(dto.getLastName().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = normalizeEmail(dto.getEmail());

            if (!customerFound.getEmail().equalsIgnoreCase(newEmail)
                    && repo.existsByEmailIgnoreCase(newEmail)) {
                throw new IllegalArgumentException("Email already in use");
            }

            customerFound.setEmail(newEmail);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            customerFound.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }

        if (dto.getNumber() != null) {
            customerFound.setNumber(dto.getNumber());
        }

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            customerFound.setRole(parseRole(dto.getRole()));
        }

        if (dto.getEnabled() != null) {
            customerFound.setEnabled(dto.getEnabled());
        }

        CustomerEntity saved = repo.save(customerFound);
        return mapper.toDto(saved);
    }

    @Override
    public CustomerDTO updateMyProfile(CustomerUpdateDTO dto, String email) {
        CustomerEntity customerFound = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            customerFound.setName(dto.getName().trim());
        }

        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            customerFound.setLastName(dto.getLastName().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = normalizeEmail(dto.getEmail());

            if (!customerFound.getEmail().equalsIgnoreCase(newEmail)
                    && repo.existsByEmailIgnoreCase(newEmail)) {
                throw new IllegalArgumentException("Email already in use");
            }

            customerFound.setEmail(newEmail);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            customerFound.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }

        if (dto.getNumber() != null) {
            customerFound.setNumber(dto.getNumber());
        }

        CustomerEntity saved = repo.save(customerFound);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteCustomer(Long id, String currentEmail) {
        CustomerEntity customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerEntity currentAdmin = repo.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new RuntimeException("Current admin not found"));

        if (currentAdmin.getId().equals(customer.getId())) {
            throw new AccessDeniedException("You cannot deactivate your own account");
        }

        customer.setEnabled(false);
        repo.save(customer);
    }

    private void validateCreate(CustomerCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Customer data is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.USER;
        }

        try {
            return Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Role must be ADMIN or USER");
        }
    }
}
