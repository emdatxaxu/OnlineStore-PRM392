package com.example.onlineshoesstoreprm392.controller;

import com.example.onlineshoesstoreprm392.entity.Cart;
import com.example.onlineshoesstoreprm392.entity.User;
import com.example.onlineshoesstoreprm392.payload.CartDto;
import com.example.onlineshoesstoreprm392.payload.CartItemDto;
import com.example.onlineshoesstoreprm392.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> getUserCart(){
        return ResponseEntity.ok(cartService.getCartByUser());
    }


    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> addToCart(@Valid @RequestBody CartItemDto cartItemDto){//needed fields: quantity, productId, Inventory

        return ResponseEntity.ok(cartService.addToCart(cartItemDto));
    }

    @DeleteMapping("/cart-item/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> removeFromCart(@PathVariable("id") Long cartItemId){

        return ResponseEntity.ok(cartService.removeFromCart(cartItemId));
    }

}
