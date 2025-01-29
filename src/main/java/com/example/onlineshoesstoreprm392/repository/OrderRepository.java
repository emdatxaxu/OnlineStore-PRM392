package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
