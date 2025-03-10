package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.MonthlyRevenueDto;
import com.example.onlineshoesstoreprm392.payload.OrderDto;
import com.example.onlineshoesstoreprm392.payload.PageableResponse;
import com.example.onlineshoesstoreprm392.payload.ProductSalesDto;

import java.math.BigDecimal;
import java.util.List;

public interface AdminDashboardService {
    List<MonthlyRevenueDto> getMonthlyRevenue();

    List<ProductSalesDto> getProductSales();

    BigDecimal getCurrentYearRevenue();

    PageableResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir);
}
