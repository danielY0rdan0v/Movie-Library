package com.example.movielibrary.models.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OmdbResponseDto(@JsonProperty("Title") String title,
                              @JsonProperty("Director") String director,
                              @JsonProperty("Year") String year,
                              @JsonProperty("imdbRating") String rating) {
}


