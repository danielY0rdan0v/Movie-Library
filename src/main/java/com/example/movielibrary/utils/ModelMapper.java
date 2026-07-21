package com.example.movielibrary.utils;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.MovieRequestDto;
import com.example.movielibrary.models.OdbmResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public Movie fromDto(MovieRequestDto movieRequestDto){
        Movie movie = new Movie();

        movie.setTitle(movieRequestDto.getTitle());
        movie.setDirector(movieRequestDto.getDirector());
        movie.setReleaseYear(movieRequestDto.getReleaseYear());

        return movie;
    }

    public Movie fromDto(OdbmResponseDto odbmResponseDto){
        Movie movie = new Movie();

        movie.setTitle(odbmResponseDto.title());
        movie.setDirector(odbmResponseDto.director());
        movie.setReleaseYear(Integer.parseInt( odbmResponseDto.year() ));
        movie.setRating(Double.parseDouble( odbmResponseDto.rating() ));

        return movie;
    }

    public Movie fromDto(MovieRequestDto dto, Movie movie){
        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setReleaseYear(dto.getReleaseYear());

        return movie;
    }


}
