package com.project.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.entity.OrderDetailEntity;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long>{
	
	List<OrderDetailEntity> findByOrderId(Long orderId);

}
