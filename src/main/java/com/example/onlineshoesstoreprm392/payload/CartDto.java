package com.example.onlineshoesstoreprm392.payload;

import com.example.onlineshoesstoreprm392.entity.CartItem;
import com.example.onlineshoesstoreprm392.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDto {

    Long id;

    BigDecimal totalPrice;

    Long userId;

    List<CartItemDto> cartItems;
}
