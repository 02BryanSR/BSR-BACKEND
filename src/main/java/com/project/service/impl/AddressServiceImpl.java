package com.project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.project.dto.AddressDTO;
import com.project.entity.AddressEntity;
import com.project.entity.CustomerEntity;
import com.project.mapper.AddressMapper;
import com.project.repository.AddressRepository;
import com.project.repository.CustomerRepository;
import com.project.service.AddressService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AddressServiceImpl implements AddressService {

	private AddressRepository addressRepo;
	private CustomerRepository customerRepo;
	private AddressMapper mapper;

	public AddressServiceImpl(AddressRepository addressRepo, CustomerRepository customerRepo, AddressMapper mapper) {

		this.addressRepo = addressRepo;
		this.customerRepo = customerRepo;
		this.mapper = mapper;
	}

	@Override
	public List<AddressDTO> findAll() {
		return mapper.toListDto(addressRepo.findAll());
	}

	@Override
	public List<AddressDTO> findByCustomerId(Long customerId) {

		if (customerId == null) {
			throw new RuntimeException("CustomerId is required");
		}
		List<AddressEntity> addresses = addressRepo.findByCustomerId(customerId);
		if (addresses.isEmpty()) {
			throw new RuntimeException("No addresses found for customerId: " + customerId);
		}
		return mapper.toListDto(addresses);
	}

	@Override
	public AddressDTO findById(Long id) {
		Optional<AddressEntity> found = addressRepo.findById(id);
		if (found.isPresent()) {
			return mapper.toDto(found.get());
		}
		throw new RuntimeException("Address not found with id: " + id);
	}

	@Override
	public AddressDTO createAddress(AddressDTO dto) {

		if (dto.getCustomerId() == null) {
			throw new RuntimeException("customerId is required");
		}
		CustomerEntity customer = customerRepo.findById(dto.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + dto.getCustomerId()));

		AddressEntity address = mapper.toEntity(dto);
		address.setCustomer(customer);
		AddressEntity saved = addressRepo.save(address);
		return mapper.toDto(saved);
	}

	@Override
	public AddressDTO updateAddress(AddressDTO dto, Long id) {

		AddressEntity address = addressRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));

		if (dto.getCustomerId() != null) {
			CustomerEntity customer = customerRepo.findById(dto.getCustomerId()).orElseThrow(
					() -> new EntityNotFoundException("Customer not found with id: " + dto.getCustomerId()));

			address.setCustomer(customer);
		}
		if (dto.getAddress() != null)
			address.setAddress(dto.getAddress());
		if (dto.getCity() != null)
			address.setCity(dto.getCity());
		if (dto.getCountry() != null)
			address.setCountry(dto.getCountry());
		if (dto.getCp() != null)
			address.setCp(dto.getCp());
		if (dto.getState() != null)
			address.setState(dto.getState());

		AddressEntity saved = addressRepo.save(address);
		return mapper.toDto(saved);
	}

	@Override
	public void deleteAddress(Long id) {
		if (!addressRepo.existsById(id)) {
			throw new EntityNotFoundException("Category not found with id: " + id);
		}
		addressRepo.deleteById(id);

	}

	@Override
	public List<AddressDTO> findMyAddresses(String email) {
		return mapper.toListDto(addressRepo.findByCustomer_EmailIgnoreCase(email));
	}

	@Override
	public AddressDTO createMyAddress(AddressDTO dto, String email) {
		CustomerEntity me = customerRepo.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found with email: " + email));

		AddressEntity address = mapper.toEntity(dto);
		address.setCustomer(me);
		return mapper.toDto(addressRepo.save(address));
	}

	@Override
	public AddressDTO updateMyAddress(AddressDTO dto, Long id, String email) {

		if (!addressRepo.existsByIdAndCustomer_EmailIgnoreCase(id, email)) {
			throw new org.springframework.security.access.AccessDeniedException("You cannot modify this address");
		}

		AddressEntity address = addressRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));

		if (dto.getAddress() != null)
			address.setAddress(dto.getAddress());
		if (dto.getCity() != null)
			address.setCity(dto.getCity());
		if (dto.getCountry() != null)
			address.setCountry(dto.getCountry());
		if (dto.getCp() != null)
			address.setCp(dto.getCp());
		if (dto.getState() != null)
			address.setState(dto.getState());

		return mapper.toDto(addressRepo.save(address));
	}

	@Override
	public void deleteMyAddress(Long id, String email) {
		if (!addressRepo.existsByIdAndCustomer_EmailIgnoreCase(id, email)) {
			throw new org.springframework.security.access.AccessDeniedException("You cannot delete this address");
		}
		addressRepo.deleteById(id);
	}

}
