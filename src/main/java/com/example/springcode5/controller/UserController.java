package com.example.springcode5.controller;

import com.example.springcode5.domain.UserEntity;
import com.example.springcode5.service.UserEntityService;
import com.example.springcode5.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserEntityService userService;

    @PostMapping("/getusers")
    public R getUsers() {
        try {
            List<UserEntity> users = userService.getUsers();
            return R.success(users);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/search")
    public R searchUsers(@RequestBody Map<String, String> params) {
        try {
            String keyword = params.get("keyword");
            List<UserEntity> users = userService.searchUsers(keyword);
            return R.success(users);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public R getUserById(@PathVariable Long id) {
        try {
            UserEntity user = userService.getUserById(id);
            return R.success(user);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/updateuser")
    public R updateUser(@RequestBody UserEntity userEntity) {
        try {
            UserEntity updatedUser = userService.updateUser(userEntity);
            return R.success(updatedUser);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/deleteuser")
    public R deleteUser(@RequestBody UserEntity userEntity) {
        try {
            userService.deleteUser(userEntity.getId());
            return R.success("删除成功");
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/updatestatus")
    public R updateUserStatus(@RequestBody UpdateStatusRequest request) {
        try {
            userService.updateUserStatus(request.getUserId(), request.getStatus());
            return R.success("状态更新成功");
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    // 内部类
    static class UpdateStatusRequest {
        private Long userId;
        private UserEntity.UserStatus status;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public UserEntity.UserStatus getStatus() {
            return status;
        }

        public void setStatus(UserEntity.UserStatus status) {
            this.status = status;
        }
    }
}