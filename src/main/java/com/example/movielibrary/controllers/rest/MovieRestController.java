package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.movie.Movie;
import com.example.movielibrary.models.movie.MovieRequestDto;
import com.example.movielibrary.models.movie.MovieResponseDto;
import com.example.movielibrary.services.MovieService;
import com.example.movielibrary.utils.ModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Manage Movies", description = "CRUD operations for Movies")
public class MovieRestController {

    private final MovieService service;
    private final ModelMapper mapper;

    @Autowired
    public MovieRestController(MovieService service,
                               ModelMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @SecurityRequirement(name = "basicAuth")
    @GetMapping()
    @Operation(summary = "Returns all Movies")
    public List<MovieResponseDto> getAll(){
        return service.getAll();
    }

    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    @Operation(summary = "Returns Movie by Id")
    public MovieResponseDto getById(@PathVariable int id){

        try{
            return mapper.toDto(service.getById(id));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @SecurityRequirement(name = "basicAuth")
    @PostMapping()
    @Operation(summary = "Creates a movie, if a movie is found in the external api it replaces the fields with the api's")
    public ResponseEntity<MovieResponseDto> create(@RequestBody MovieRequestDto dto){

        try{
            MovieResponseDto movie = mapper.toDto(service.create(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(movie);
        }catch (DuplicateEntityException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}")
    @Operation(summary = "Updates a Movie by Id")
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

    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a Movie")
    public ResponseEntity<Void> delete(@PathVariable int id){

        try{
            service.delete(id);
            return ResponseEntity.noContent().build();
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        }
    }
}
