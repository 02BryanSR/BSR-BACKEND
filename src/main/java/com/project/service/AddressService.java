package com.project.service;

import java.util.List;
import com.project.dto.AddressDTO;


public interface AddressService {

	List<AddressDTO> findAll();
	List<AddressDTO> findByCustomerId(Long customerId);
	AddressDTO findById(Long id);
	AddressDTO createAddress(AddressDTO dto);
	AddressDTO updateAddress(AddressDTO dto, Long id);
	void deleteAddress(Long id);

	List<AddressDTO> findMyAddresses(String email);
	AddressDTO createMyAddress(AddressDTO dto, String email);
	AddressDTO updateMyAddress(AddressDTO dto, Long id, String email);
	void deleteMyAddress(Long id, String email);
}
