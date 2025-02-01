package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Category;
import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.ProductMapper;
import com.example.onlineshoesstoreprm392.payload.ProductDto;
import com.example.onlineshoesstoreprm392.payload.ProductResponse;
import com.example.onlineshoesstoreprm392.repository.CategoryRepository;
import com.example.onlineshoesstoreprm392.repository.ProductRepository;
import com.example.onlineshoesstoreprm392.service.ProductService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;


    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));

        //convert DTO to entity
        Product product = productMapper.toProduct(productDto);
        product.setCategory(category);
        product.setCreated_at(new Timestamp(System.currentTimeMillis()));
        product.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        product.setDeleted(false);

        //link images and inventories to product
        product.getImages().stream().forEach(image -> image.setProduct(product));
        product.getInventories().stream().forEach(inventory -> inventory.setProduct(product));

        //save
        Product newProduct = productRepository.save(product);

        //convert entity to DTO
        ProductDto productResponse = productMapper.toProductDto(newProduct);

        return productResponse;
    }

    @Override
    public ProductResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> products = productRepository.findAll(pageable);

        //get content for page object
        List<Product> listOfProducts = products.getContent();

        List<ProductDto> content =  listOfProducts.stream().map(product -> productMapper.toProductDto(product))
                .collect(Collectors.toList());

        ProductResponse productResponse = ProductResponse.builder()
                .content(content)
                .pageNo(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .last(products.isLast())
                .build();
        return productResponse;
    }

    @Override
    public ProductDto getProductById(Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product","id",id));

        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto dto, Long id) {
        return null;
    }

    @Override
    public void deleteProductById(Long id) {

    }

    @Override
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return null;
    }
}
