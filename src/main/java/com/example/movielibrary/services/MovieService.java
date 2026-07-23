package com.example.movielibrary.services;

import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;

import java.util.List;

public interface MovieService {

    List<MovieResponseDto> getAll();

    Movie getById(int id);


    Movie create(MovieRequestDto dto);
    Movie update(Movie movie);
    void delete(int id);

}
