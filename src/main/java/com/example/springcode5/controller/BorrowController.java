package com.example.springcode5.controller;

import com.example.springcode5.domain.BorrowRecord;
import com.example.springcode5.domain.SecurityUser;
import com.example.springcode5.dto.BorrowSearchDTO;
import com.example.springcode5.service.BorrowService;
import com.example.springcode5.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.example.springcode5.dto.PageRequestDTO;
import com.example.springcode5.dto.BookSearchDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // 借书 - 只有管理员可以操作
    @PostMapping("/borrow")
    @PreAuthorize("hasRole('ADMIN')")
    public R borrowBook(@RequestBody BorrowRequest request) {
        try {
            BorrowRecord record = borrowService.borrowBook(request.getUserId(), request.getBookId());
            return R.success(convertRecordToMap(record));
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 还书 - 只有管理员可以操作
    @PostMapping("/return")
    @PreAuthorize("hasRole('ADMIN')")
    public R returnBook(@RequestBody ReturnRequest request) {
        try {
            BorrowRecord record = borrowService.returnBook(request.getRecordId());
            return R.success(convertRecordToMap(record));
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 续借 - 只有管理员可以操作
    @PostMapping("/renew")
    @PreAuthorize("hasRole('ADMIN')")
    public R renewBook(@RequestBody RenewRequest request) {
        try {
            BorrowRecord record = borrowService.renewBook(request.getRecordId());
            return R.success(convertRecordToMap(record));
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 查询个人借阅记录 - 读者和管理员都可以
    @PostMapping("/my-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'READER')")
    public R getMyBorrowRecords(@RequestBody PageRequestDTO request) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            Long userId = securityUser.getUserEntity().getId();

            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "borrowDate")
            );
            Page<BorrowRecord> records = borrowService.getUserBorrowRecords(userId, pageable);

            // 转换结果
            List<Map<String, Object>> content = records.getContent().stream()
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("totalElements", records.getTotalElements());
            result.put("totalPages", records.getTotalPages());
            result.put("currentPage", records.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 查询指定用户的借阅记录 - 只有管理员可以操作
    @PostMapping("/user-records")
    @PreAuthorize("hasRole('ADMIN')")
    public R getUserBorrowRecords(@RequestBody UserRecordsRequest request) {
        try {
            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "borrowDate")
            );
            Page<BorrowRecord> records = borrowService.getUserBorrowRecords(request.getUserId(), pageable);

            // 转换结果
            List<Map<String, Object>> content = records.getContent().stream()
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("totalElements", records.getTotalElements());
            result.put("totalPages", records.getTotalPages());
            result.put("currentPage", records.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 查询所有借阅记录 - 只有管理员可以操作
    @PostMapping("/all-records")
    @PreAuthorize("hasRole('ADMIN')")
    public R getAllBorrowRecords(@RequestBody PageRequestDTO request) {
        try {
            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "borrowDate")
            );
            Page<BorrowRecord> records = borrowService.getAllBorrowRecords(pageable);

            // 转换结果
            List<Map<String, Object>> content = records.getContent().stream()
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("totalElements", records.getTotalElements());
            result.put("totalPages", records.getTotalPages());
            result.put("currentPage", records.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 搜索借阅记录 - 只有管理员可以操作
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public R searchBorrowRecords(@RequestBody BorrowSearchDTO request) {
        try {
            Pageable pageable = PageRequest.of(
                    request.getPage(),
                    request.getSize(),
                    Sort.by(Sort.Direction.DESC, "borrowDate")
            );

            BorrowRecord.BorrowStatus status = null;
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                status = BorrowRecord.BorrowStatus.valueOf(request.getStatus());
            }

            Page<BorrowRecord> records = borrowService.searchBorrowRecords(
                    request.getKeyword(),
                    status,
                    pageable
            );

            // 转换结果
            List<Map<String, Object>> content = records.getContent().stream()
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("content", content);
            result.put("totalElements", records.getTotalElements());
            result.put("totalPages", records.getTotalPages());
            result.put("currentPage", records.getNumber());

            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 获取逾期记录 - 只有管理员可以操作
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public R getOverdueRecords() {
        try {
            List<BorrowRecord> records = borrowService.getOverdueRecords();
            // 转换结果
            List<Map<String, Object>> result = records.stream()
                    .map(this::convertRecordToMap)
                    .collect(Collectors.toList());
            return R.success(result);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 辅助方法：转换借阅记录为Map，将枚举状态转换为字符串
    private Map<String, Object> convertRecordToMap(BorrowRecord record) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("user", record.getUser());
        map.put("book", record.getBook());
        map.put("borrowDate", record.getBorrowDate());
        map.put("expectedReturnDate", record.getExpectedReturnDate());
        map.put("actualReturnDate", record.getActualReturnDate());
        map.put("status", record.getStatus() != null ? record.getStatus().name() : null);  // 枚举转字符串
        map.put("renewCount", record.getRenewCount());
        map.put("fine", record.getFine());
        map.put("createTime", record.getCreateTime());
        map.put("updateTime", record.getUpdateTime());
        return map;
    }

    // 内部类
    static class BorrowRequest {
        private Long userId;
        private Long bookId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }
    }

    static class ReturnRequest {
        private Long recordId;

        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }
    }

    static class RenewRequest {
        private Long recordId;

        public Long getRecordId() {
            return recordId;
        }

        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }
    }

    static class UserRecordsRequest extends PageRequestDTO {
        private Long userId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}