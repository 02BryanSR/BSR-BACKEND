package com.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.dto.OrderDetailDTO;
import com.project.entity.OrderDetailEntity;
import com.project.entity.OrderEntity;
import com.project.mapper.OrderDetailsMapper;
import com.project.repository.OrderDetailRepository;
import com.project.repository.OrderRepository;
import com.project.service.OrderDetailService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository detailRepo;
    private final OrderRepository orderRepo;
    private final OrderDetailsMapper mapper;

    public OrderDetailServiceImpl(OrderDetailRepository detailRepo,
                                  OrderRepository orderRepo,
                                  OrderDetailsMapper mapper) {
        this.detailRepo = detailRepo;
        this.orderRepo = orderRepo;
        this.mapper = mapper;
    }

    @Override
    public List<OrderDetailDTO> findByOrderId(Long orderId) {
        List<OrderDetailEntity> orders = detailRepo.findByOrderId(orderId);
        return mapper.toListDtos(orders);
    }

    @Override
    public List<OrderDetailDTO> findMyOrderDetails(Long orderId, String email) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Access denied");
        }

        List<OrderDetailEntity> orders = detailRepo.findByOrderId(orderId);
        return mapper.toListDtos(orders);
    }
}