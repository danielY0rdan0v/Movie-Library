package com.example.movielibrary.repositories;

import com.example.movielibrary.models.Movie;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MovieRepositoryImpl implements MovieRepository{

    private final EntityManager entityManager;

    @Autowired
    public MovieRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public List<Movie> getAll() {

        return entityManager.createQuery("FROM Movie", Movie.class)
                .getResultList();
    }

    @Override
    public Movie getById(int id) {
        return entityManager.createQuery("FROM Movie WHERE id =:id", Movie.class)
                .setParameter("id", id)
                .getSingleResult(); //TODO catch no result exception
    }

    @Override
    public Movie getByTitle(String title) {
        return entityManager.createQuery("FROM Movie WHERE title = :title", Movie.class)
                .setParameter("title", title)
                .getSingleResult(); //TODO catch no result exception
    }

    @Override
    @Transactional
    public void create(Movie movie) {

        entityManager.persist(movie);

    }

    @Override
    @Transactional
    public void delete(Movie movie) {

        entityManager.remove(movie);
    }

    @Override
    public void update(Movie movie) {

        entityManager.merge(movie);

    }
}
