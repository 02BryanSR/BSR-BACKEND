package com.project.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.project.dto.AddressDTO;
import com.project.service.AddressService;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

	private final AddressService service;

	public AddressController(AddressService service) {
		this.service = service;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<AddressDTO>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<AddressDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));

	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO dto) {
		AddressDTO created = service.createAddress(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{id}")
	public ResponseEntity<AddressDTO> updateAddress(@RequestBody AddressDTO dto, @PathVariable Long id) {
		AddressDTO updated = service.updateAddress(dto, id);
		return ResponseEntity.ok(updated);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		
		service.deleteAddress(id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/me")
	public ResponseEntity<List<AddressDTO>> myAddresses(Authentication auth) {
		return ResponseEntity.ok(service.findMyAddresses(auth.getName())); // email
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PostMapping("/me")
	public ResponseEntity<AddressDTO> createMyAddress(@RequestBody AddressDTO dto, Authentication auth) {
		AddressDTO created = service.createMyAddress(dto, auth.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PatchMapping("/me/{id}")
	public ResponseEntity<AddressDTO> updateMyAddress(@RequestBody AddressDTO dto, @PathVariable Long id,
			Authentication auth) {
		return ResponseEntity.ok(service.updateMyAddress(dto, id, auth.getName()));
	}

	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@DeleteMapping("/me/{id}")
	public ResponseEntity<Void> deleteMyAddress(@PathVariable Long id, Authentication auth) {
		service.deleteMyAddress(id, auth.getName());
		return ResponseEntity.noContent().build();
	}

}
