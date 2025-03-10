package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.PageableResponse;
import com.example.onlineshoesstoreprm392.payload.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto, List<MultipartFile> images);
    PageableResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);
    ProductDto getProductById(Long id);
    ProductDto updateProduct(ProductDto dto, Long id);
    void deleteProductById(Long id);
    PageableResponse getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir);
    PageableResponse searchProducts(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);
    List<ProductDto> getPopularProduct();
}
