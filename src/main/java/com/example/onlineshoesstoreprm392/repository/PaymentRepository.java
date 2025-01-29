package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
