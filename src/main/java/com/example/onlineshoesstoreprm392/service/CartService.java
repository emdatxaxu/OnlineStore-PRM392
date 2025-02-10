package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.CartDto;
import com.example.onlineshoesstoreprm392.payload.CartItemDto;

public interface CartService {
    CartDto getCartByUser();
    CartDto addToCart(CartItemDto cartItemDto);
    CartDto removeFromCart(Long cartItemId);
    CartDto updateCart(CartItemDto cartItemDto, Long cartItemId);//cart item quantity update
}
