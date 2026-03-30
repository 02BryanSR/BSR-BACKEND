package com.project.service;

import java.util.List;
import com.project.dto.OrderDetailDTO;


public interface OrderDetailService {


    List<OrderDetailDTO> findByOrderId(Long orderId);

    List<OrderDetailDTO> findMyOrderDetails(Long orderId, String email);
}
