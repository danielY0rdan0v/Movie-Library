package com.example.movielibrary.services;

import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.Movie;
import com.example.movielibrary.repositories.MovieRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    MovieRepository repository;

    @Autowired
    public MovieServiceImpl(MovieRepository repository){
        this.repository = repository;
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
    public Movie getByTitle(String title) {
        try{
            return repository.getByTitle(title);
        }catch (NoResultException e){
            throw new EntityNotFoundException("Not Found!"); //TODO fix
        }
    }

    @Override
    public void create(Movie movie) {

        repository.create(movie);
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
