package com.example.movielibrary.services;

import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;

import java.util.List;

public interface MovieService {

    List<Movie> getAll();

    Movie getById(int id);


    Movie create(MovieRequestDto dto);
    void update(Movie movie);
    void delete(int id);

}
