package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.payload.MonthlyRevenueDto;
import com.example.onlineshoesstoreprm392.utils.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    Optional<Product> findByIdAndDeleted(Long id, boolean deleted);

    Page<Product> findByCategoryIdAndDeleted(Long categoryId, boolean deleted, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND p.deleted = false")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.created_at DESC")
    List<Product> findTop5Newest(Pageable pageable);
}
