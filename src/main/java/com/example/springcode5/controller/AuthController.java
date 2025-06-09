package com.example.springcode5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.springcode5.domain.UserEntity;
import com.example.springcode5.service.AuthService;
import com.example.springcode5.service.UserEntityService;
import com.example.springcode5.utils.R;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserEntityService userService;

    @PostMapping("/login")
    public R login(@RequestBody UserEntity params) {
        try {
            String username = params.getUsername();
            String password = params.getPassword();

            if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
                return R.error(500, "用户名或密码为空！");
            }

            String token = authService.login(username, password);
            if (token == null) {
                return R.error(500, "用户名或密码错误！");
            }

            UserEntity user = userService.getUserByName(username);

            // 构建返回的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("usertype", user.getUsertype());
            userInfo.put("realName", user.getRealName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("borrowLimit", user.getBorrowLimit());
            userInfo.put("currentBorrowCount", user.getCurrentBorrowCount());
            userInfo.put("status", user.getStatus());
            userInfo.put("regionCode", token); // 保持兼容性，将token放在regionCode中

            return R.success(userInfo);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/add/user")
    public R addUser(@RequestBody UserEntity userEntity) {
        try {
            if (!StringUtils.hasText(userEntity.getUsername()) ||
                    !StringUtils.hasText(userEntity.getPassword()) ||
                    !StringUtils.hasText(userEntity.getUsertype())) {
                return R.error(500, "用户名、密码和用户类型不能为空！");
            }

            UserEntity entity = authService.addUser(userEntity);
            return R.success(entity);
        } catch (Exception e) {
            return R.error(500, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public R logout() {
        // 这里可以添加清除Redis中token的逻辑
        return R.success("登出成功");
    }
}