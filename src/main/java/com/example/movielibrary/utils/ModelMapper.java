package com.example.movielibrary.utils;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.MovieRequestDto;
import com.example.movielibrary.services.MovieService;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    private MovieService service;

    public ModelMapper(MovieService service){
        this.service =service;
    }

    public Movie fromDto(MovieRequestDto dto){
        Movie movie = new Movie();

        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setReleaseYear(dto.getReleaseYear());

        return movie;
    }

    public Movie fromDto(int id, MovieRequestDto dto){
        Movie movie = service.getById(id);

        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setReleaseYear(dto.getReleaseYear());

        return movie;
    }


}
