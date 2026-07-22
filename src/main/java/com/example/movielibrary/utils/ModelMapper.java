package com.example.movielibrary.utils;

import com.example.movielibrary.models.*;
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

    public User fromDto(UserRequestDto dto){

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        return user;

    }
    public User fromDtoUpdate(UpdateUserRequestDto dto){

        User user = new User();
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return user;

    }

    private double tryParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


}
