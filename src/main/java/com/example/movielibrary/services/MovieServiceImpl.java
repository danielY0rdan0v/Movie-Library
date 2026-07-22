package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.OdbmResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    MovieRepository repository;
    ExternalApiService apiService;
    ModelMapper mapper;

    @Autowired
    public MovieServiceImpl(MovieRepository repository,
                            ExternalApiService service,
                            ModelMapper mapper){
        this.repository = repository;
        this.apiService = service;
        this.mapper = mapper;
    }

    @Override
    public List<Movie> getAll() {
        return repository.getAll();
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
    public void create(Movie movie) {


        OdbmResponseDto dto = apiService.searchMovie(movie.getTitle(), movie.getReleaseYear());

        if (dto.title() != null && dto.year() != null) {
            Movie movieFromApi = mapper.fromDto(dto);
            repository.create(movieFromApi);
        } else {
            repository.create(movie);
        }

    }

    @Override
    public void update(Movie movie) {

        repository.update(movie);
    }

    @Override
    public void delete(int id) {

        Movie movie = getById(id);
        repository.delete(movie);
    }
}
