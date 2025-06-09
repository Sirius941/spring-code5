package com.example.springcode5.service;

import java.util.List;

import com.example.springcode5.domain.UserEntity;

public interface UserEntityService {
    UserEntity getUserByName(String username);
    UserEntity getUserById(Long id);
    UserEntity saveUser(UserEntity userEntity);
    UserEntity updateUser(UserEntity userEntity);
    void deleteUser(Long userId);
    List<UserEntity> getUsers();
    List<UserEntity> searchUsers(String keyword);
    void updateUserStatus(Long userId, UserEntity.UserStatus status);
}
