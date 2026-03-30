package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.CartItemEntity;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByCartId(Long cartId);

    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, Long productId);

    List<CartItemEntity> findByCartCustomerId(Long customerId);
}
