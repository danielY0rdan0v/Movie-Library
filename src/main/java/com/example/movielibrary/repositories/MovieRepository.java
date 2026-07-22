package com.example.movielibrary.repositories;

import com.example.movielibrary.models.Movie;

import java.util.List;

public interface MovieRepository {

    List<Movie> getAll();

    Movie getById(int id);

    void create(Movie movie);
    void delete(Movie movie);
    void update(Movie movie);
}
