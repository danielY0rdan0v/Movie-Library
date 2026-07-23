package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAll() {
        return repository.getAll();
    }

    @Override
    public User getById(int id) {
        User user = repository.getById(id);

        if (user == null){
            throw new EntityNotFoundException("User with id " + id + " was not found!");
        }
        return user;
    }

    @Override
    public void create(User user) {

        checkIfUserExists(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.create(user);

    }

    @Override
    public void update(User user, int id) {

        getById(id);
        repository.update(user);
    }

    @Override
    public void delete(int id) {

        User user = getById(id);
        repository.delete(user);
    }

    private void checkIfUserExists(User user){
        if (repository.isEmailExist(user.getEmail())){
            throw new DuplicateEntityException(
                    "User with email: " + user.getEmail() + " already exists!"
            );
        }
        if (repository.isUsernameExist(user.getUsername())){
            throw new DuplicateEntityException(
                    "User with username: " + user.getUsername() + " already exists!"
            );
        }

    }
}
