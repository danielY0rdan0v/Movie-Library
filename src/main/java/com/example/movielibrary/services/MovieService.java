package com.example.movielibrary.services;

import com.example.movielibrary.models.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getAll();

    Movie getById(int id);

    Movie getByTitle(String title);

    void create(Movie movie);
    void update(Movie movie);
    void delete(int id);

}
