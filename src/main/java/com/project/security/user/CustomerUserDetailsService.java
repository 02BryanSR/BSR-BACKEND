package com.project.security.user;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.project.repository.CustomerRepository;

@Service 
public class CustomerUserDetailsService implements UserDetailsService { // esta clase conecta Security con tu base de datos.

	private final CustomerRepository customerRepository;

	public CustomerUserDetailsService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return customerRepository.findByEmailIgnoreCase(email).map(UserPrincipal::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
	}

}
