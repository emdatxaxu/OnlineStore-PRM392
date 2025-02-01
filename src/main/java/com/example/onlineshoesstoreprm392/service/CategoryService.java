package com.example.onlineshoesstoreprm392.service;

import com.example.onlineshoesstoreprm392.payload.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    CategoryDto getCategory(Long id);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);
    void deleteCategory(Long categoryId);
}

