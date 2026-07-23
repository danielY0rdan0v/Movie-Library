package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;
import com.example.movielibrary.services.MovieService;
import com.example.movielibrary.utils.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public List<MovieResponseDto> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MovieResponseDto getById(@PathVariable int id){

        try{
            return mapper.toDto(service.getById(id));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<MovieResponseDto> create(@RequestBody MovieRequestDto dto){

        MovieResponseDto movie = mapper.toDto(service.create(dto));
        return  ResponseEntity.status(HttpStatus.CREATED).body(movie);

    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDto> update(@PathVariable int id, @RequestBody MovieRequestDto dto){

        try{
            Movie movie = service.getById(id);
            Movie movieToUpdate = mapper.fromDto(dto, movie);
            Movie updated = service.update(movieToUpdate);
            return ResponseEntity.ok(mapper.toDto(updated));

        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){

        try{
            service.delete(id);
            return ResponseEntity.noContent().build();
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        }
    }
}
