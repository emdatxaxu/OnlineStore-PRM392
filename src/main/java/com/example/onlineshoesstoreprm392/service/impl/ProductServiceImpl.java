package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Category;
import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.ProductMapper;
import com.example.onlineshoesstoreprm392.payload.ImageDto;
import com.example.onlineshoesstoreprm392.payload.PageableResponse;
import com.example.onlineshoesstoreprm392.payload.ProductDto;
import com.example.onlineshoesstoreprm392.repository.CategoryRepository;
import com.example.onlineshoesstoreprm392.repository.ProductRepository;
import com.example.onlineshoesstoreprm392.service.ProductService;
import com.example.onlineshoesstoreprm392.utils.FileUtility;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    public ProductDto createProduct(ProductDto productDto, List<MultipartFile> images) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));

        //check images
        FileUtility.checkImages(images);

        //save image then add image's url to product dto
        List<ImageDto> listImageDto = new ArrayList<>();
        for(MultipartFile img : images){
            String imagePath = System.getProperty("user.dir")+"\\img\\"
                    +System.currentTimeMillis()+img.getOriginalFilename();
            listImageDto.add(new ImageDto(imagePath));
            FileUtility fileUtility = new FileUtility();
            fileUtility.saveFile(img, imagePath);
        }

        productDto.setImages(listImageDto);

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
    public PageableResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> products = productRepository.findAll(pageable);

        //get content for page object
        List<Product> listOfProducts = products.getContent();

        List<ProductDto> content =  listOfProducts.stream().map(product -> productMapper.toProductDto(product))
                .collect(Collectors.toList());

        PageableResponse productResponse = new PageableResponse<ProductDto>(content, products.getNumber(),
                products.getSize(), products.getTotalElements(), products.getTotalPages(), products.isLast());

        return productResponse;
    }

    @Override
    public ProductDto getProductById(Long id) {

        Product product = productRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",id));

        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto, Long id) {

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",id));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(product.getDescription());
        product.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toProductDto(updatedProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public PageableResponse getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> products = productRepository.findByCategoryIdAndDeleted(categoryId, false, pageable);

        //get content for page object
        List<Product> listOfProducts = products.getContent();

        List<ProductDto> content =  listOfProducts.stream().map(product -> productMapper.toProductDto(product))
                .collect(Collectors.toList());

        PageableResponse productResponse = new PageableResponse<ProductDto>(content, products.getNumber(),
                products.getSize(), products.getTotalElements(), products.getTotalPages(), products.isLast());

        return productResponse;
    }

    @Override
    public PageableResponse searchProducts(String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        //create pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> products = productRepository.findByKeyword(keyword, pageable);

        //get content for page object
        List<Product> listOfProducts = products.getContent();

        List<ProductDto> content =  listOfProducts.stream().map(product -> productMapper.toProductDto(product))
                .collect(Collectors.toList());

        PageableResponse productResponse = new PageableResponse<ProductDto>(content, products.getNumber(),
                products.getSize(), products.getTotalElements(), products.getTotalPages(), products.isLast());

        return productResponse;
    }

    @Override
    public List<ProductDto> getPopularProduct() {
        List<Product> products = productRepository.findTop5Newest(PageRequest.of(0,5));
        return products.stream().map(product -> productMapper.toProductDto(product)).collect(Collectors.toList());
    }


}
