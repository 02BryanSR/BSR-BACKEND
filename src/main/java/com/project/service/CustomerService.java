package com.project.service;

import java.util.List;

import com.project.dto.CustomerCreateDTO;
import com.project.dto.CustomerDTO;
import com.project.dto.CustomerUpdateDTO;

public interface CustomerService {

    List<CustomerDTO> findAll();

    List<CustomerDTO> findByLastName(String lastName);

    CustomerDTO findById(Long id);

    CustomerDTO findByEmailIgnoreCase(String email);

    CustomerDTO createCustomer(CustomerCreateDTO dto);

    CustomerDTO updateCustomer(CustomerUpdateDTO dto, Long id, String currentEmail);

    CustomerDTO updateMyProfile(CustomerUpdateDTO dto, String email);

    void deleteCustomer(Long id, String currentEmail);
}
