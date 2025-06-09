package com.example.springcode5.service;

import com.example.springcode5.domain.Book;
import com.example.springcode5.domain.BookCategory;
import com.example.springcode5.repo.BookRepository;
import com.example.springcode5.repo.BookCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository categoryRepository;

    @Override
    public Book saveBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("ISBN已存在");
        }

        // 验证分类是否存在
        if (book.getCategory() != null && book.getCategory().getId() != null) {
            BookCategory category = categoryRepository.findById(book.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("图书分类不存在"));
            book.setCategory(category);
        }

        // 初始化可借数量等于总库存
        book.setAvailableCount(book.getTotalCount());

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        Book existingBook = getBookById(book.getId());

        // 如果修改了ISBN，检查新ISBN是否已存在
        if (!existingBook.getIsbn().equals(book.getIsbn())
                && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("ISBN已存在");
        }

        // 验证分类是否存在
        if (book.getCategory() != null && book.getCategory().getId() != null) {
            BookCategory category = categoryRepository.findById(book.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("图书分类不存在"));
            existingBook.setCategory(category);
        }

        existingBook.setIsbn(book.getIsbn());
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setPublishDate(book.getPublishDate());
        existingBook.setPrice(book.getPrice());
        existingBook.setDescription(book.getDescription());
        existingBook.setTotalCount(book.getTotalCount());
        existingBook.setCoverImage(book.getCoverImage());
        existingBook.setLocation(book.getLocation());
        existingBook.setStatus(book.getStatus());

        // 重新计算可借数量
        int borrowedCount = existingBook.getTotalCount() - existingBook.getAvailableCount();
        existingBook.setAvailableCount(book.getTotalCount() - borrowedCount);

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        book.setStatus(Book.BookStatus.DELETED);
        bookRepository.save(book);
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Long categoryId, Pageable pageable) {
        return bookRepository.searchBooks(keyword, categoryId, pageable);
    }

    @Override
    public Page<Book> getAvailableBooks(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
    }

    @Override
    public List<Book> getBooksByCategory(Long categoryId) {
        BookCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        return bookRepository.findByCategory(category);
    }

    @Override
    @Transactional
    public void updateBookStock(Long bookId, Integer change) {
        Book book = getBookById(bookId);
        int newAvailableCount = book.getAvailableCount() + change;

        if (newAvailableCount < 0) {
            throw new RuntimeException("库存不足");
        }

        if (newAvailableCount > book.getTotalCount()) {
            throw new RuntimeException("可借数量不能超过总库存");
        }

        book.setAvailableCount(newAvailableCount);

        // 如果没有可借图书，更新状态
        if (newAvailableCount == 0) {
            book.setStatus(Book.BookStatus.UNAVAILABLE);
        } else {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }

        bookRepository.save(book);
    }
}