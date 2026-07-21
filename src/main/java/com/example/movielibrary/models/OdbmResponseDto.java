package com.example.movielibrary.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OdbmResponseDto(@JsonProperty("Title") String title,
                              @JsonProperty("Director") String director,
                              @JsonProperty("Year") String year,
                              @JsonProperty("imdbRating") String rating) {
}


