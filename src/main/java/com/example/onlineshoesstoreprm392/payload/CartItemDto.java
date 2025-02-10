package com.example.onlineshoesstoreprm392.payload;

import com.example.onlineshoesstoreprm392.entity.Cart;
import com.example.onlineshoesstoreprm392.entity.Inventory;
import com.example.onlineshoesstoreprm392.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemDto {
    Long id;

    String name;

    String image;

    BigDecimal unitPrice;

    @Min(1)
    int quantity;

    BigDecimal totalPrice;

    @NotNull
    Long productId;

    InventoryDto inventory;

    Long cartId;
}
