package com.example.movielibrary.services;

import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAll();

    User getById(int id);

    User create(User user);
    User update(User user, int id);
    void delete(int id);
}
