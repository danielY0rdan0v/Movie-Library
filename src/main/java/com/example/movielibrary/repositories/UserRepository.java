package com.example.movielibrary.repositories;

import com.example.movielibrary.models.user.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User getById(int id);
    User getByUsername(String username);

    boolean isEmailExist(String email);
    boolean isUsernameExist(String username);

    User create(User user);

    User update(User user);

    void delete(User user);


}
