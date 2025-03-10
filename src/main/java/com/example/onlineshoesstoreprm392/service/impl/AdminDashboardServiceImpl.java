package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Order;
import com.example.onlineshoesstoreprm392.mapper.OrderMapper;
import com.example.onlineshoesstoreprm392.payload.*;
import com.example.onlineshoesstoreprm392.repository.OrderRepository;
import com.example.onlineshoesstoreprm392.service.AdminDashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private OrderRepository orderRepository;
    private OrderMapper orderMapper;

    public AdminDashboardServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        return orderRepository.getMonthlyRevenue().stream()
                .map(obj -> new MonthlyRevenueDto((Integer) obj[0], (BigDecimal)obj[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductSalesDto> getProductSales() {
        return orderRepository.getProductSales(PageRequest.of(0,5)).stream()
                .map(obj -> new ProductSalesDto((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getCurrentYearRevenue() {
        return (BigDecimal) orderRepository.getCurrentYearRevenue();
    }

    @Override
    public PageableResponse<OrderDto> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> orders = orderRepository.findAll(pageable);

        List<Order> orderList = orders.getContent();

        List<OrderDto> content = orderList.stream().map(order -> orderMapper.toOrderDto(order))
                .collect(Collectors.toList());

        PageableResponse orderResponse = new PageableResponse<OrderDto>(content, orders.getNumber(),
                orders.getSize(), orders.getTotalElements(), orders.getTotalPages(), orders.isLast());

        return orderResponse;
    }
}
