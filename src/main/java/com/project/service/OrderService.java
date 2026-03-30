package com.project.service;

import java.util.List;

import com.project.dto.CreateOrderRequestDTO;
import com.project.dto.OrderDTO;
import com.project.entity.enums.OrderStatus;

public interface OrderService {

    List<OrderDTO> findAll();

    List<OrderDTO> findByCustomerId(Long customerId);

    List<OrderDTO> findByStatus(OrderStatus status);

    OrderDTO findById(Long id);

    OrderDTO createOrder(OrderDTO dto);

    OrderDTO updateStatus(Long orderId, OrderStatus status);

    List<OrderDTO> findMyOrders(String email);

    OrderDTO findMyOrderById(Long orderId, String email);

    OrderDTO createMyOrder(CreateOrderRequestDTO dto, String email);
}
