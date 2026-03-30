package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByEmailIgnoreCase(String email);

    List<CustomerEntity> findByLastNameContaining(String lastName);

	boolean existsByEmailIgnoreCase(String email);

}