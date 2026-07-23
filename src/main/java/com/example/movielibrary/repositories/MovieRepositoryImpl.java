package com.example.movielibrary.repositories;

import com.example.movielibrary.models.movie.Movie;
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

        return entityManager.find(Movie.class, id);
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
    @Transactional
    public Movie update(Movie movie) {

        return entityManager.merge(movie);

    }
}
