package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
