package com.project.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.entity.CustomerEntity;
import com.project.entity.enums.Role;
import com.project.repository.CustomerRepository;
import com.project.security.dto.*;
import com.project.security.jwt.JwtService;
import com.project.security.user.UserPrincipal;

@Service
public class AuthService { // solo sirve para login y registro -> dto, no bloquea rutas

	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
			throw new IllegalArgumentException("Email already in use");
		}

		CustomerEntity user = new CustomerEntity();
		user.setName(request.getName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.USER);
		user.setEnabled(true);

		CustomerEntity saved = customerRepository.save(user);
		String token = jwtService.generateToken(saved.getEmail(), saved.getId(), saved.getRole().name());

		return new AuthResponse(token);
	}

	public AuthResponse login(LoginRequest request) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
		String token = jwtService.generateToken(principal.getUsername(), principal.getId(), principal.getRole());
		return new AuthResponse(token);
	}
}
