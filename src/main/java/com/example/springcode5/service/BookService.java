package com.example.springcode5.service;

import com.example.springcode5.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    Book saveBook(Book book);
    Book updateBook(Book book);
    void deleteBook(Long id);
    Book getBookById(Long id);
    Book getBookByIsbn(String isbn);
    Page<Book> getAllBooks(Pageable pageable);
    Page<Book> searchBooks(String keyword, Long categoryId, Pageable pageable);
    Page<Book> getAvailableBooks(Pageable pageable);
    List<Book> getBooksByCategory(Long categoryId);
    void updateBookStock(Long bookId, Integer change);
}