package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Category;
import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.ProductMapper;
import com.example.onlineshoesstoreprm392.payload.ImageDto;
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
        if(images.isEmpty()){
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST,
                "No product's image added yet.");
        }
        for(MultipartFile img : images){
            String contentType = img.getContentType();
            if(!isValidImage(contentType)){
                throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST,
                        "Invalid file type. Only JPG, JPEG and PNG allowed.");
            }
        }
        //save image then add image's url to product dto
        List<ImageDto> listImageDto = new ArrayList<>();
        for(MultipartFile img : images){
            String imagePath = saveImage(img);
            listImageDto.add(new ImageDto(imagePath));
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
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        List<Product> products = productRepository.findByCategoryIdAndDeleted(categoryId, false);

        return products.stream().map(product -> productMapper.toProductDto(product))
                .collect(Collectors.toList());
    }

    private boolean isValidImage(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg");
    }

    private String saveImage(MultipartFile file){
        String filePath = System.getProperty("user.dir")+"\\img\\"+file.getOriginalFilename();

        try {
            File convertFile = new File(filePath);
            convertFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        }catch (IOException ex){
            throw new OnlineStoreAPIException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some error occur when processing file");
        }

        return filePath;
    }
}
