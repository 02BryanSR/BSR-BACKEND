package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.FavoriteEntity;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {

    List<FavoriteEntity> findByCustomerIdOrderByCreateDateDesc(Long customerId);

    Optional<FavoriteEntity> findByCustomerIdAndProductId(Long customerId, Long productId);

    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
}
