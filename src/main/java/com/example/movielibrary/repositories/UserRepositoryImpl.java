package com.example.movielibrary.repositories;

import com.example.movielibrary.models.User;
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
