package com.example.onlineshoesstoreprm392.service.impl;

import com.example.onlineshoesstoreprm392.entity.Category;
import com.example.onlineshoesstoreprm392.exception.ResourceNotFoundException;
import com.example.onlineshoesstoreprm392.mapper.CategoryMapper;
import com.example.onlineshoesstoreprm392.payload.CategoryDto;
import com.example.onlineshoesstoreprm392.repository.CategoryRepository;
import com.example.onlineshoesstoreprm392.service.CategoryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto getCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(category -> categoryMapper.toCategoryDto(category))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.deleteById(categoryId);
    }

}
