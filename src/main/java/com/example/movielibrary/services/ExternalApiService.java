package com.example.movielibrary.services;

import com.example.movielibrary.models.omdb.OmdbResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalApiService {

    private final RestClient restClient;

    @Value("${omdb.api.key}")
    private String apikey;

    @Autowired
    public ExternalApiService(RestClient restClient){
        this.restClient = restClient;
    }

    public OmdbResponseDto searchMovie(String title, int year){

        return restClient.get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path("/")
                                        .queryParam("apikey", apikey)
                                        .queryParam("t", title)
                                        .queryParam("y", year)
                                        .build())
                .retrieve()
                .body(OmdbResponseDto.class);
    }

}
