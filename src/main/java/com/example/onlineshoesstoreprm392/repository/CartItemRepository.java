package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
