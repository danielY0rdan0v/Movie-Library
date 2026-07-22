package com.example.movielibrary.utils;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.MovieRequestDto;
import com.example.movielibrary.models.MovieResponseDto;
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
        movie.setRating(tryParseDouble(odbmResponseDto.rating(), 0.0));
        return movie;
    }

    public Movie fromDto(MovieRequestDto dto, Movie movie){
        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setReleaseYear(dto.getReleaseYear());

        return movie;
    }

    public MovieResponseDto toDto(Movie movie){
        MovieResponseDto dto = new MovieResponseDto();
        dto.setTitle(movie.getTitle());
        dto.setDirector(movie.getDirector());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setRating(movie.getRating());

        return dto;
    }

    private double tryParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


}
