package com.example.springcode5.service;

import com.example.springcode5.domain.BookCategory;
import com.example.springcode5.repo.BookCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BookCategoryServiceImpl implements BookCategoryService {

    @Autowired
    private BookCategoryRepository categoryRepository;

    @Override
    public BookCategory saveCategory(BookCategory category) {
        if (existsByCategoryCode(category.getCategoryCode())) {
            throw new RuntimeException("分类编码已存在");
        }
        return categoryRepository.save(category);
    }

    @Override
    public BookCategory updateCategory(BookCategory category) {
        BookCategory existingCategory = getCategoryById(category.getId());

        // 如果修改了分类编码，检查新编码是否已存在
        if (!existingCategory.getCategoryCode().equals(category.getCategoryCode())
                && existsByCategoryCode(category.getCategoryCode())) {
            throw new RuntimeException("分类编码已存在");
        }

        existingCategory.setCategoryCode(category.getCategoryCode());
        existingCategory.setCategoryName(category.getCategoryName());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("分类不存在");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public BookCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    @Override
    public List<BookCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<BookCategory> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories();
        }
        return categoryRepository.searchByKeyword(keyword);
    }

    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        return categoryRepository.existsByCategoryCode(categoryCode);
    }
}