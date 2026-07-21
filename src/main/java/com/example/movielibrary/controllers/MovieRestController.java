package com.example.movielibrary.controllers;

import com.example.movielibrary.models.Movie;
import com.example.movielibrary.models.MovieRequestDto;
import com.example.movielibrary.services.MovieService;
import com.example.movielibrary.utils.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieRestController {

    private final MovieService service;
    private final ModelMapper mapper;

    public MovieRestController(MovieService service,
                               ModelMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public List<Movie> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Movie getById(@PathVariable int id){

        return service.getById(id);
    }

    @PostMapping()
    public void create(@RequestBody MovieRequestDto dto){

        Movie movie = mapper.fromDto(dto);
        service.create(movie);

    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody MovieRequestDto dto){

        Movie movie = mapper.fromDto(id, dto);
        service.update(movie);

    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){

        service.delete(id);

    }
}
