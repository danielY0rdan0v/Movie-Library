package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserResponseDto;
import com.example.movielibrary.repositories.UserRepository;
import com.example.movielibrary.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder,
                           ModelMapper mapper){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    public List<UserResponseDto> getAll() {
        return repository.getAll().stream().map(mapper::toDto).toList();
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
    public User create(User user) {

        checkIfUserExists(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.create(user);

    }

    @Override
    public User update(User user, int id) {

        User existingUser = getById(id);

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());

        if (user.getPassword() != null && !user.getPassword().isBlank()){
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return repository.update(existingUser);
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
