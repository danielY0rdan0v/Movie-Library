package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    MovieRepository repository;
    MovieEnrichmentService enrichmentService;
    ModelMapper mapper;

    @Autowired
    public MovieServiceImpl(MovieRepository repository,
                            MovieEnrichmentService enrichmentService,
                            ModelMapper mapper){
        this.repository = repository;
        this.enrichmentService = enrichmentService;
        this.mapper = mapper;
    }

    @Override
    public List<MovieResponseDto> getAll() {

        return repository
                .getAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Movie getById(int id) {

        Movie movie = repository.getById(id);
        if (movie != null){
            return movie;
        }else {
            throw new EntityNotFoundException("Movie with id " + id + " was Not Found!");
        }
    }


    @Override
    public Movie create(MovieRequestDto dto) {

        Movie movie = mapper.fromDto(dto);
        repository.create(movie);

        enrichmentService.enrichMovieAsync(movie.getId(), movie.getTitle(), movie.getReleaseYear());

        return movie;
    }

    @Override
    public Movie update(Movie movie) {

        return repository.update(movie);
    }

    @Override
    public void delete(int id) {

        Movie movie = getById(id);
        repository.delete(movie);
    }
}
