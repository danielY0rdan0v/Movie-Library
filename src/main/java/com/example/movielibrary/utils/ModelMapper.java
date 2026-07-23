package com.example.movielibrary.utils;

import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;
import com.example.movielibrary.models.movie.Status;
import com.example.movielibrary.models.omdb.OmdbResponseDto;
import com.example.movielibrary.models.user.UpdateUserRequestDto;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public Movie fromDto(MovieRequestDto movieRequestDto){
        Movie movie = new Movie();

        movie.setTitle(movieRequestDto.getTitle());
        movie.setDirector(movieRequestDto.getDirector());
        movie.setReleaseYear(movieRequestDto.getReleaseYear());
        movie.setStatus(Status.PENDING);

        return movie;
    }

    public Movie fromDto(OmdbResponseDto omdbResponseDto){
        Movie movie = new Movie();

        movie.setTitle(omdbResponseDto.title());
        movie.setDirector(omdbResponseDto.director());
        movie.setReleaseYear(Integer.parseInt( omdbResponseDto.year() ));
        movie.setRating(tryParseDouble(omdbResponseDto.rating(), 0.0));
        return movie;
    }
    public Movie fromDto(Movie movie, OmdbResponseDto omdbResponseDto){

        movie.setTitle(omdbResponseDto.title());
        movie.setDirector(omdbResponseDto.director());
        movie.setReleaseYear(Integer.parseInt( omdbResponseDto.year() ));
        movie.setRating(tryParseDouble(omdbResponseDto.rating(), 0.0));
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
