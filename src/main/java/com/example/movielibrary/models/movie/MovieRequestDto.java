package com.example.movielibrary.models.movie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MovieRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String director;
    @NotNull
    @Min(value = 1895, message = "Invalid Year, the first film was filmed in 1895 year!")
    private int releaseYear;


    public MovieRequestDto(){

    }

    public MovieRequestDto(String title, String director, int releaseYear) {
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
}
