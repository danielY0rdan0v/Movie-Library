package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.OdbmResponseDto;
import com.example.movielibrary.repositories.MovieRepository;
import com.example.movielibrary.utils.ModelMapper;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    MovieRepository repository;
    ExternalApiService service;
    ModelMapper mapper;

    @Autowired
    public MovieServiceImpl(MovieRepository repository,
                            ExternalApiService service,
                            ModelMapper mapper){
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public List<Movie> getAll() {
        return repository.getAll();
    }

    @Override
    public Movie getById(int id) {
        try{
            return repository.getById(id);
        }catch (NoResultException e){
            throw new EntityNotFoundException("Not Found!"); //TODO fix
        }
    }



    @Override
    public void create(Movie movie) {


            OdbmResponseDto dto = service.searchByTitle(movie.getTitle());

            if (dto.title() != null) {
                Movie movieFromApi = mapper.fromDto(dto);
                repository.create(movieFromApi);
            }else {
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
