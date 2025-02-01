package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Category;
import com.example.onlineshoesstoreprm392.entity.Product;
import com.example.onlineshoesstoreprm392.payload.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {InventoryMapper.class, ImageMapper.class, FeedbackMapper.class})
public interface ProductMapper {
    Product toProduct(ProductDto dto);

    @Mapping(target = "categoryId", source = "category", qualifiedByName = "mapCategoryId")
    ProductDto toProductDto(Product post);

    @Named("mapCategoryId")
    default Long mapCategoryId(Category category){
        return category.getId();
    }
}
