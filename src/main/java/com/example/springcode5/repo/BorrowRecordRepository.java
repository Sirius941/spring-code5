package com.example.springcode5.repo;

import com.example.springcode5.domain.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // 根据用户ID查询借阅记录
    Page<BorrowRecord> findByUser_Id(Long userId, Pageable pageable);

    // 根据状态查询
    Page<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status, Pageable pageable);

    // 检查用户是否已借阅某本书（未归还）
    @Query("SELECT COUNT(br) > 0 FROM BorrowRecord br " +
            "WHERE br.user.id = :userId " +
            "AND br.book.id = :bookId " +
            "AND br.status IN :statuses")
    boolean existsByUserIdAndBookIdAndStatusIn(@Param("userId") Long userId,
                                               @Param("bookId") Long bookId,
                                               @Param("statuses") List<BorrowRecord.BorrowStatus> statuses);

    // 关键词搜索（搜索用户名、真实姓名、图书名称、ISBN）
    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.user.username LIKE %:keyword% " +
            "OR br.user.realName LIKE %:keyword% " +
            "OR br.book.title LIKE %:keyword% " +
            "OR br.book.isbn LIKE %:keyword%")
    Page<BorrowRecord> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 根据关键词和状态搜索
    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.status = :status " +
            "AND (br.user.username LIKE %:keyword% " +
            "OR br.user.realName LIKE %:keyword% " +
            "OR br.book.title LIKE %:keyword% " +
            "OR br.book.isbn LIKE %:keyword%)")
    Page<BorrowRecord> findByKeywordAndStatus(@Param("keyword") String keyword,
                                              @Param("status") BorrowRecord.BorrowStatus status,
                                              Pageable pageable);

    // 查询用户当前借阅的图书数量
    @Query("SELECT COUNT(br) FROM BorrowRecord br " +
            "WHERE br.user.id = :userId " +
            "AND br.status IN (com.example.springcode5.domain.BorrowRecord$BorrowStatus.BORROWED, com.example.springcode5.domain.BorrowRecord$BorrowStatus.RENEWED)")
    long countActiveByUserId(@Param("userId") Long userId);

    // 查询逾期记录
    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.expectedReturnDate < :currentDate " +
            "AND br.status IN (com.example.springcode5.domain.BorrowRecord$BorrowStatus.BORROWED, com.example.springcode5.domain.BorrowRecord$BorrowStatus.RENEWED)")
    List<BorrowRecord> findOverdueRecords(@Param("currentDate") Date currentDate);

    // 无参数版本的查询逾期记录（供定时任务使用）
    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.expectedReturnDate < CURRENT_DATE " +
            "AND br.status IN (com.example.springcode5.domain.BorrowRecord$BorrowStatus.BORROWED, com.example.springcode5.domain.BorrowRecord$BorrowStatus.RENEWED)")
    List<BorrowRecord> findOverdueRecords();
}