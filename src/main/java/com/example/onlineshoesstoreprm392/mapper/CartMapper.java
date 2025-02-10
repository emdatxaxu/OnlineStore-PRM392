package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Cart;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.payload.CartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {
    Cart toCart(CartDto cartDto);

    @Mapping(target = "userId", source = "user", qualifiedByName = "mapUserId")
    CartDto toCartDto(Cart cart);

    @Named("mapUserId")
    default Long mapUserId(User user){ return user.getId();}
}
