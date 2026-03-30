package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.OrderEntity;
import com.project.entity.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByCustomerId(Long customerId);

	List<OrderEntity> findByStatus(OrderStatus status);
}
