package com.example.onlineshoesstoreprm392.controller;

import com.example.onlineshoesstoreprm392.payload.ProductDto;
import com.example.onlineshoesstoreprm392.payload.ProductResponse;
import com.example.onlineshoesstoreprm392.service.ProductService;
import com.example.onlineshoesstoreprm392.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProduct(@Valid @ModelAttribute ProductDto productDto,
                                                    @RequestPart("files") List<MultipartFile> files){
        return new ResponseEntity<>(productService.createProduct(productDto, files), HttpStatus.CREATED);
    }


    //get all product rest api
    @GetMapping
    public ProductResponse getAllProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return productService.getAllProducts(pageNo, pageSize, sortBy, sortDir);
    }

    //search product by keyword rest api
    @GetMapping("/search")
    public ProductResponse searchProducts(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){
        return productService.searchProducts(keyword, pageNo, pageSize, sortBy, sortDir);
    }

    //get product by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }


    //update
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto,
                                                 @PathVariable(name = "id") Long id){
        return ResponseEntity.ok(productService.updateProduct(productDto, id));
    }



    //delete
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") Long id){
        productService.deleteProductById(id);
        return ResponseEntity.ok("Product Deleted Successfully!");
    }

    //get product by category api
    //http://localhost:8080/api/posts/category/2
    @GetMapping("/category/{id}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable(name = "id") Long categoryId){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

}
