package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Cart;
import com.example.onlineshoesstoreprm392.entity.CartItem;
import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.payload.CartItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = InventoryMapper.class)
public interface CartItemMapper {
    CartItem toCartItem(CartItemDto cartItemDto);

    @Mapping(target = "cartId", source = "cart", qualifiedByName = "mapCartId")
    @Mapping(target = "productId", source = "product", qualifiedByName = "mapProductId")
    CartItemDto toCartItemDto(CartItem cartItem);

    @Named("mapCartId")
    default Long mapCartId(Cart cart){ return cart.getId();}

    @Named("mapProductId")
    default Long mapProductId(Product product){ return product.getId();}
}
