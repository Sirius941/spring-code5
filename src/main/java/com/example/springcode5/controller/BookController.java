package com.example.springcode5.controller;

import com.example.springcode5.domain.Book;
import com.example.springcode5.dto.PageRequestDTO;
import com.example.springcode5.dto.BookSearchDTO;
import com.example.springcode5.service.BookService;
import com.example.springcode5.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public R addBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.saveBook(book);
            return R.success(savedBook);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/update")
    public R updateBook(@RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(book);
            return R.success(updatedBook);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/delete")
    public R deleteBook(@RequestBody Book book) {
        try {
            bookService.deleteBook(book.getId());
            return R.success("删除成功");
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public R getBookById(@PathVariable Long id) {
        try {
            Book book = bookService.getBookById(id);
            return R.success(book);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/list")
    public R getAllBooks(@RequestBody PageRequestDTO request) {
        try {
            // 使用Spring Data的PageRequest创建Pageable对象
            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "createTime")
            );
            Page<Book> books = bookService.getAllBooks(pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", books.getContent());
            result.put("totalElements", books.getTotalElements());
            result.put("totalPages", books.getTotalPages());
            result.put("currentPage", books.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/search")
    public R searchBooks(@RequestBody BookSearchDTO request) {
        try {
            // 使用Spring Data的PageRequest创建Pageable对象
            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "createTime")
            );
            Page<Book> books = bookService.searchBooks(
                    request.getKeyword(),
                    request.getCategoryId(),
                    pageable
            );

            Map<String, Object> result = new HashMap<>();
            result.put("content", books.getContent());
            result.put("totalElements", books.getTotalElements());
            result.put("totalPages", books.getTotalPages());
            result.put("currentPage", books.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/available")
    public R getAvailableBooks(@RequestBody PageRequestDTO request) {
        try {
            // 使用Spring Data的PageRequest创建Pageable对象
            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "createTime")
            );
            Page<Book> books = bookService.getAvailableBooks(pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", books.getContent());
            result.put("totalElements", books.getTotalElements());
            result.put("totalPages", books.getTotalPages());
            result.put("currentPage", books.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public R getBooksByCategory(@PathVariable Long categoryId) {
        try {
            List<Book> books = bookService.getBooksByCategory(categoryId);
            return R.success(books);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }
}