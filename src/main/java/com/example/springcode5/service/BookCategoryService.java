// BookCategoryService.java
package com.example.springcode5.service;

import com.example.springcode5.domain.BookCategory;
import java.util.List;

public interface BookCategoryService {
    BookCategory saveCategory(BookCategory category);
    BookCategory updateCategory(BookCategory category);
    void deleteCategory(Long id);
    BookCategory getCategoryById(Long id);
    List<BookCategory> getAllCategories();
    List<BookCategory> searchCategories(String keyword);
    boolean existsByCategoryCode(String categoryCode);
}



