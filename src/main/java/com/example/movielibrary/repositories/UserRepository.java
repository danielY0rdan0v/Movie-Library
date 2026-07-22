package com.example.movielibrary.repositories;

import com.example.movielibrary.models.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User getById(int id);

    void create(User user);

    void update(User user);

    void delete(User user);


}
