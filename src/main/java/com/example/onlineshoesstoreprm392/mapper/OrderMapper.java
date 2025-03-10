package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Order;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.payload.OrderDto;
import com.example.onlineshoesstoreprm392.payload.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderDto dto);

    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUserId")
    OrderDto toOrderDto(Order order);

    @Named("mapUserId")
    default Long mapUserId(User user){
        return user.getId();
    }
}
