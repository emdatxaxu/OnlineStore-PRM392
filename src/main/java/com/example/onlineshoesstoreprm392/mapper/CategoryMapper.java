package com.example.onlineshoesstoreprm392.mapper;

import com.example.onlineshoesstoreprm392.entity.Category;
import com.example.onlineshoesstoreprm392.payload.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDto categoryDto);
    CategoryDto toCategoryDto(Category category);
}
