package com.example.springcode5.service;

import com.example.springcode5.domain.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BorrowService {

    /**
     * 借阅图书
     * @param userId 借阅人ID
     * @param bookId 图书ID
     * @return 借阅记录
     */
    BorrowRecord borrowBook(Long userId, Long bookId);

    /**
     * 归还图书
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    BorrowRecord returnBook(Long recordId);

    /**
     * 续借图书
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    BorrowRecord renewBook(Long recordId);

    /**
     * 获取用户的借阅记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 借阅记录分页数据
     */
    Page<BorrowRecord> getUserBorrowRecords(Long userId, Pageable pageable);

    /**
     * 获取所有借阅记录
     * @param pageable 分页参数
     * @return 借阅记录分页数据
     */
    Page<BorrowRecord> getAllBorrowRecords(Pageable pageable);

    /**
     * 搜索借阅记录
     * @param keyword 关键词
     * @param status 状态
     * @param pageable 分页参数
     * @return 借阅记录分页数据
     */
    Page<BorrowRecord> searchBorrowRecords(String keyword, BorrowRecord.BorrowStatus status, Pageable pageable);

    /**
     * 获取逾期记录
     * @return 逾期的借阅记录列表
     */
    List<BorrowRecord> getOverdueRecords();

    /**
     * 检查并更新逾期记录
     */
    void checkAndUpdateOverdueRecords();
}