package com.example.movielibrary.repositories;

import com.example.movielibrary.models.user.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User getById(int id);

    boolean isEmailExist(String email);
    boolean isUsernameExist(String username);

    void create(User user);

    void update(User user);

    void delete(User user);


}
