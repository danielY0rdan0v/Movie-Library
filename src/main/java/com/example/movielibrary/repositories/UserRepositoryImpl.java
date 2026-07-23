package com.example.movielibrary.repositories;

import com.example.movielibrary.models.user.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final EntityManager entityManager;

    @Autowired
    public UserRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public List<User> getAll() {
        return entityManager.createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public User getById(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public boolean isEmailExist(String email) {
        List<User> user =  entityManager.createQuery("FROM User WHERE email = :email", User.class)
                .setParameter("email", email)
                .getResultList();

        return !user.isEmpty();
    }

    @Override
    public boolean isUsernameExist(String username) {
        List<User> user =  entityManager.createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .getResultList();

        return !user.isEmpty();
    }

    @Transactional
    @Override
    public void create(User user) {

        entityManager.persist(user);
    }

    @Transactional
    @Override
    public void update(User user) {

        entityManager.merge(user);
    }

    @Transactional
    @Override
    public void delete(User user) {

        entityManager.remove(user);
    }
}
