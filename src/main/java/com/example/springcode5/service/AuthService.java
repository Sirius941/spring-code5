package com.example.springcode5.service;

import com.example.springcode5.domain.UserEntity;

public interface AuthService {
    String login(String username, String password);
    UserEntity addUser(UserEntity userEntity);
}