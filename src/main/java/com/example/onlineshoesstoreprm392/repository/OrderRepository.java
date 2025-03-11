package com.example.onlineshoesstoreprm392.repository;

import com.example.onlineshoesstoreprm392.entity.Order;
import com.example.onlineshoesstoreprm392.payload.MonthlyRevenueDto;
import com.example.onlineshoesstoreprm392.payload.ProductSalesDto;
import com.example.onlineshoesstoreprm392.utils.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT FUNCTION('MONTH', o.orderDate) AS month, SUM(o.totalPrice) AS revenue " +
            "FROM Order o " +
            "WHERE o.status = \""+ OrderStatus.PAID +"\" "+
            "AND FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE) "+
            "GROUP BY FUNCTION('MONTH', o.orderDate) " +
            "ORDER BY FUNCTION('MONTH', o.orderDate) desc ")
    List<Object[]> getMonthlyRevenue();

    @Query("SELECT SUM(o.totalPrice) " +
            "FROM Order o " +
            "WHERE o.status = \""+ OrderStatus.PAID +"\" "+
            "AND FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE)")
    Object getCurrentYearRevenue();

    @Query("SELECT ot.name, SUM(ot.quantity) AS quantity FROM OrderItem ot GROUP BY ot.name " +
            "ORDER BY SUM(ot.quantity) DESC ")
    List<Object[]> getProductSales(Pageable pageable);


    Page<Order> findByUserId(Long userId, Pageable pageable);



}
