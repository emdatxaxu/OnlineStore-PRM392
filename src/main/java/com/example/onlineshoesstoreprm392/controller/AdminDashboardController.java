package com.example.onlineshoesstoreprm392.controller;

import com.example.onlineshoesstoreprm392.payload.MonthlyRevenueDto;
import com.example.onlineshoesstoreprm392.payload.OrderDto;
import com.example.onlineshoesstoreprm392.payload.PageableResponse;
import com.example.onlineshoesstoreprm392.payload.ProductSalesDto;
import com.example.onlineshoesstoreprm392.service.AdminDashboardService;
import com.example.onlineshoesstoreprm392.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin-dashboard")
public class AdminDashboardController {

    private AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDto>> getMonthlyRevenue(){
        return ResponseEntity.ok(adminDashboardService.getMonthlyRevenue());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/product-sales")
    public ResponseEntity<List<ProductSalesDto>> getTop5ProductSales(){
        return ResponseEntity.ok(adminDashboardService.getProductSales());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/year-revenue")
    public ResponseEntity<BigDecimal> getCurrentYearRevenue(){
        return ResponseEntity.ok(adminDashboardService.getCurrentYearRevenue());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<PageableResponse<OrderDto>> getAllOrders(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return ResponseEntity.ok(adminDashboardService.getAllOrders(pageNo, pageSize, sortBy, sortDir));
    }

}
