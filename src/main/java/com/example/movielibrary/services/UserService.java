package com.example.movielibrary.services;

import com.example.movielibrary.models.user.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(int id);

    void create(User user);
    void update(User user, int id);
    void delete(int id);
}
