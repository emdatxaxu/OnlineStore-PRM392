package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.ProductDto;
import com.example.onlineshoesstoreprm392.payload.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);
    ProductDto getProductById(Long id);
    ProductDto updateProduct(ProductDto dto, Long id);
    void deleteProductById(Long id);
    List<ProductDto> getProductsByCategory(Long categoryId);
}
