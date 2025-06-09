package com.example.springcode5.controller;

import com.example.springcode5.domain.BookCategory;
import com.example.springcode5.service.BookCategoryService;
import com.example.springcode5.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class BookCategoryController {

    @Autowired
    private BookCategoryService categoryService;

    @PostMapping("/add")
    public R addCategory(@RequestBody BookCategory category) {
        try {
            BookCategory savedCategory = categoryService.saveCategory(category);
            return R.success(savedCategory);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/update")
    public R updateCategory(@RequestBody BookCategory category) {
        try {
            BookCategory updatedCategory = categoryService.updateCategory(category);
            return R.success(updatedCategory);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/delete")
    public R deleteCategory(@RequestBody BookCategory category) {
        try {
            categoryService.deleteCategory(category.getId());
            return R.success("删除成功");
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public R getCategoryById(@PathVariable Long id) {
        try {
            BookCategory category = categoryService.getCategoryById(id);
            return R.success(category);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/list")
    public R getAllCategories() {
        try {
            List<BookCategory> categories = categoryService.getAllCategories();
            return R.success(categories);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/search")
    public R searchCategories(@RequestBody SearchRequest request) {
        try {
            List<BookCategory> categories = categoryService.searchCategories(request.getKeyword());
            return R.success(categories);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 内部类用于接收搜索请求
    static class SearchRequest {
        private String keyword;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }
}