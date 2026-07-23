package com.example.movielibrary.services;

import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.omdb.OmdbResponseDto;
import com.example.movielibrary.models.movie.Status;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.utils.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MovieEnrichmentService {

    private final ExternalApiService omdbClient;
    private final MovieRepository repository;
    private final ModelMapper mapper;

    public MovieEnrichmentService(ExternalApiService omdbClient,
                                  MovieRepository repository,
                                  ModelMapper mapper){
        this.omdbClient = omdbClient;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Async("taskExecutor")
    public void enrichMovieAsync(int id, String title, int year){

        Movie movie = repository.getById(id);

        if (movie == null){
            return;
        }

        try{
            OmdbResponseDto data = omdbClient.searchMovie(title,year);
            if (data == null || data.title() == null){
                movie.setStatus(Status.FAILED);
            }else {
                movie = mapper.fromDto(movie, data);
                movie.setStatus(Status.ENRICHED);
            }
        }catch (Exception e){
            movie.setStatus(Status.FAILED);

        }

        repository.update(movie);
    }
}
