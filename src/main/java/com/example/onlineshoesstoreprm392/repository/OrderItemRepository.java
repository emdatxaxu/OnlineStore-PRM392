package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
