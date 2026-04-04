package com.project.security;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.entity.CustomerEntity;
import com.project.entity.PasswordResetTokenEntity;
import com.project.entity.enums.Role;
import com.project.exception.EmailNotFoundException;
import com.project.exception.InvalidResetTokenException;
import com.project.repository.CustomerRepository;
import com.project.repository.PasswordResetTokenRepository;
import com.project.security.dto.AuthResponse;
import com.project.security.dto.LoginRequest;
import com.project.security.dto.RegisterRequest;
import com.project.security.jwt.JwtService;
import com.project.security.user.UserPrincipal;
import com.project.service.EmailService;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.security.reset-password-expiration-ms}")
    private long resetPasswordExpirationMs;

    public AuthService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        CustomerEntity user = new CustomerEntity();
        user.setName(request.getName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        CustomerEntity saved = customerRepository.save(user);
        String token = jwtService.generateToken(saved.getEmail(), saved.getId(), saved.getRole().name());

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtService.generateToken(
                principal.getUsername(),
                principal.getId(),
                principal.getRole()
        );

        return new AuthResponse(token);
    }

    public void forgotPassword(String email) {
        String normalizedEmail = normalizeEmail(email);

        CustomerEntity customer = customerRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new EmailNotFoundException("El correo no está registrado"));

        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setToken(UUID.randomUUID().toString());
        tokenEntity.setCustomer(customer);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(resetPasswordExpirationMs / 1000));
        tokenEntity.setUsed(false);

        passwordResetTokenRepository.save(tokenEntity);

        String resetLink = frontendUrl + "/reset-password?token=" + tokenEntity.getToken();

        emailService.sendPasswordResetEmail(customer.getEmail(), resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidResetTokenException("El token no es válido"));

        if (tokenEntity.isUsed()) {
            throw new InvalidResetTokenException("El token ya ha sido utilizado");
        }

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("El token ha expirado");
        }

        CustomerEntity customer = tokenEntity.getCustomer();
        customer.setPassword(passwordEncoder.encode(newPassword.trim()));
        customerRepository.save(customer);

        tokenEntity.setUsed(true);
        passwordResetTokenRepository.save(tokenEntity);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}