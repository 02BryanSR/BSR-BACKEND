package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.project.dto.OrderDTO;
import com.project.entity.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {

	@Mapping(target = "customer", ignore = true)
	@Mapping(target = "address", ignore = true)
	@Mapping(target = "orderDetails", ignore = true)
	@Mapping(target = "status", ignore = true)
	OrderEntity toEntity(OrderDTO dto);

	@Mapping(target = "customerId", source = "customer.id")
	@Mapping(target = "addressId", source = "address.id")
	@Mapping(target = "orderDetailIds", ignore = true)
	@Mapping(target = "status", expression = "java(entity.getStatus() == null ? null : entity.getStatus().name())")
	OrderDTO toDto(OrderEntity entity);

	List<OrderEntity> toListEntities(List<OrderDTO> ListDTO);

	List<OrderDTO> toListDtos(List<OrderEntity> listEntities);
}
