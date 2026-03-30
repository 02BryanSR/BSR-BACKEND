package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.project.dto.OrderDetailDTO;
import com.project.entity.OrderDetailEntity;

@Mapper(componentModel = "spring")
public interface OrderDetailsMapper {

	@Mapping(target = "order", ignore = true)
	@Mapping(target = "product", ignore = true)
	OrderDetailEntity toEntity(OrderDetailDTO dto);

	@Mapping(target = "orderId", source = "order.id")
	@Mapping(target = "productId", source = "product.id")
	OrderDetailDTO toDto(OrderDetailEntity entity);

	List<OrderDetailEntity> toListEntities(List<OrderDetailDTO> listDtos);

	List<OrderDetailDTO> toListDtos(List<OrderDetailEntity> listEntities);
}
