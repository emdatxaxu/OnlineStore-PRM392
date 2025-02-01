package com.example.onlineshoesstoreprm392.controller;

import com.example.onlineshoesstoreprm392.payload.ProductDto;
import com.example.onlineshoesstoreprm392.payload.ProductResponse;
import com.example.onlineshoesstoreprm392.service.ProductService;
import com.example.onlineshoesstoreprm392.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(
        name = "CRUD REST APIs for PRODUCT Resource"
)
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //create product
    @Operation(
            summary = "Create PRODUCT REST API",
            description = "is used to add new PRODUCT to database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 Created"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> createPost(@Valid @RequestBody ProductDto productDto){
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }


    //get all post rest api
    @GetMapping
    public ProductResponse getAllProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return productService.getAllProducts(pageNo, pageSize, sortBy, sortDir);
    }

    //get post by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getPostById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }


}
