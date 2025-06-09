package com.example.springcode5.service;

import com.example.springcode5.domain.Book;
import com.example.springcode5.domain.BorrowRecord;
import com.example.springcode5.domain.UserEntity;
import com.example.springcode5.repo.BookRepository;
import com.example.springcode5.repo.BorrowRecordRepository;
import com.example.springcode5.repo.UserEntityRepository;
import com.example.springcode5.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Override
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        // 1. 检查用户是否存在
        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 检查用户状态
        if (user.getStatus() != UserEntity.UserStatus.ACTIVE) {
            throw new RuntimeException("用户状态异常，无法借阅");
        }

        // 3. 检查用户借阅限制
        if (user.getCurrentBorrowCount() >= user.getBorrowLimit()) {
            throw new RuntimeException("已达到最大借阅数量限制");
        }

        // 4. 检查图书是否存在
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 5. 检查图书是否可借
        if (book.getAvailableCount() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 6. 检查是否已经借阅过该书（未归还）
        boolean alreadyBorrowed = borrowRecordRepository.existsByUserIdAndBookIdAndStatusIn(
                userId, bookId,
                Arrays.asList(BorrowRecord.BorrowStatus.BORROWED, BorrowRecord.BorrowStatus.RENEWED)
        );

        if (alreadyBorrowed) {
            throw new RuntimeException("该用户已经借阅了这本书，请先归还后再借阅");
        }

        // 7. 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUser(user);  // 设置借阅人为指定的用户
        record.setBook(book);
        record.setBorrowDate(new Date());
        record.setExpectedReturnDate(addDays(new Date(), 30));  // 默认借阅期限30天
        record.setStatus(BorrowRecord.BorrowStatus.BORROWED);
        record.setRenewCount(0);
        record.setFine(BigDecimal.ZERO);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        // 8. 更新图书库存
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookRepository.save(book);

        // 9. 更新用户当前借阅数量
        user.setCurrentBorrowCount(user.getCurrentBorrowCount() + 1);
        userEntityRepository.save(user);

        // 10. 保存借阅记录
        return borrowRecordRepository.save(record);
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        // 1. 查找借阅记录
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        // 2. 检查状态
        if (record.getStatus() == BorrowRecord.BorrowStatus.RETURNED) {
            throw new RuntimeException("该图书已经归还");
        }

        // 3. 更新借阅记录
        record.setActualReturnDate(new Date());
        record.setStatus(BorrowRecord.BorrowStatus.RETURNED);
        record.setUpdateTime(new Date());

        // 4. 计算罚金（如果逾期）
        Date now = new Date();
        if (now.after(record.getExpectedReturnDate())) {
            long diffInMillies = Math.abs(now.getTime() - record.getExpectedReturnDate().getTime());
            long overdueDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            BigDecimal fine = BigDecimal.valueOf(overdueDays * 0.5);  // 每天0.5元
            record.setFine(fine);
        }

        // 5. 更新图书库存
        Book book = record.getBook();
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookRepository.save(book);

        // 6. 更新用户当前借阅数量
        UserEntity user = record.getUser();
        user.setCurrentBorrowCount(Math.max(0, user.getCurrentBorrowCount() - 1));
        userEntityRepository.save(user);

        return borrowRecordRepository.save(record);
    }

    @Override
    @Transactional
    public BorrowRecord renewBook(Long recordId) {
        // 1. 查找借阅记录
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        // 2. 检查状态
        if (record.getStatus() != BorrowRecord.BorrowStatus.BORROWED &&
                record.getStatus() != BorrowRecord.BorrowStatus.RENEWED) {
            throw new RuntimeException("只有借阅中的图书才能续借");
        }

        // 3. 检查续借次数
        if (record.getRenewCount() >= 2) {
            throw new RuntimeException("已达到最大续借次数");
        }

        // 4. 检查是否已逾期
        if (new Date().after(record.getExpectedReturnDate())) {
            throw new RuntimeException("图书已逾期，请先归还");
        }

        // 5. 更新借阅记录
        record.setExpectedReturnDate(addDays(record.getExpectedReturnDate(), 30));
        record.setRenewCount(record.getRenewCount() + 1);
        record.setStatus(BorrowRecord.BorrowStatus.RENEWED);
        record.setUpdateTime(new Date());

        return borrowRecordRepository.save(record);
    }

    @Override
    public Page<BorrowRecord> getUserBorrowRecords(Long userId, Pageable pageable) {
        return borrowRecordRepository.findByUser_Id(userId, pageable);
    }

    @Override
    public Page<BorrowRecord> getAllBorrowRecords(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable);
    }

    @Override
    public Page<BorrowRecord> searchBorrowRecords(String keyword, BorrowRecord.BorrowStatus status, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty() && status != null) {
            return borrowRecordRepository.findByKeywordAndStatus(keyword, status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return borrowRecordRepository.findByKeyword(keyword, pageable);
        } else if (status != null) {
            return borrowRecordRepository.findByStatus(status, pageable);
        } else {
            return borrowRecordRepository.findAll(pageable);
        }
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords();
    }

    @Override
    @Transactional
    public void checkAndUpdateOverdueRecords() {
        // 查找所有应该标记为逾期的记录
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords();

        for (BorrowRecord record : overdueRecords) {
            // 更新状态为逾期
            record.setStatus(BorrowRecord.BorrowStatus.OVERDUE);

            // 计算罚金
            Date now = new Date();
            long diffInMillies = Math.abs(now.getTime() - record.getExpectedReturnDate().getTime());
            long overdueDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            BigDecimal fine = BigDecimal.valueOf(overdueDays * 0.5);  // 每天0.5元
            record.setFine(fine);

            record.setUpdateTime(new Date());
            borrowRecordRepository.save(record);
        }
    }

    // 辅助方法：给日期添加天数
    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}