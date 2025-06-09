package com.example.springcode5.service;

import com.example.springcode5.domain.UserEntity;
import com.example.springcode5.repo.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Override
    public UserEntity getUserByName(String username) {
        return userEntityRepository.getUserByName(username);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public UserEntity saveUser(UserEntity entity) {
        // 检查用户名是否已存在
        if (userEntityRepository.existsByUsername(entity.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        // 设置默认值
        if (entity.getStatus() == null) {
            entity.setStatus(UserEntity.UserStatus.ACTIVE);
        }
        if (entity.getBorrowLimit() == null) {
            entity.setBorrowLimit(5);
        }
        entity.setCurrentBorrowCount(0);

        // 如果是管理员，设置更高的借阅限制
        if ("admin".equals(entity.getUsertype())) {
            entity.setBorrowLimit(10);
        }

        return userEntityRepository.save(entity);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        UserEntity existingUser = getUserById(user.getId());

        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername())
                && userEntityRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 更新用户信息
        existingUser.setUsername(user.getUsername());
        existingUser.setUsertype(user.getUsertype());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setRealName(user.getRealName());
        existingUser.setAddress(user.getAddress());
        existingUser.setIdCard(user.getIdCard());

        // 如果提供了新密码，则更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userEntityRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        UserEntity user = getUserById(userId);
        user.setStatus(UserEntity.UserStatus.DELETED);
        userEntityRepository.save(user);
    }

    @Override
    public List<UserEntity> getUsers() {
        return userEntityRepository.findAllActive();
    }

    @Override
    public List<UserEntity> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getUsers();
        }
        return userEntityRepository.searchUsers(keyword);
    }

    @Override
    public void updateUserStatus(Long userId, UserEntity.UserStatus status) {
        userEntityRepository.updateUserStatus(userId, status);
    }
}