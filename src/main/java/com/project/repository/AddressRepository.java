package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.entity.AddressEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

	List<AddressEntity> findByCustomerId(Long customerId);

	List<AddressEntity> findByCustomer_EmailIgnoreCase(String email);

	boolean existsByIdAndCustomer_EmailIgnoreCase(Long id, String email);
}
