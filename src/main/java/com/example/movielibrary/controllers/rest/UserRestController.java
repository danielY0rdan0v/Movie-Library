package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.UpdateUserRequestDto;
import com.example.movielibrary.models.User;
import com.example.movielibrary.models.UserRequestDto;
import com.example.movielibrary.services.UserService;
import com.example.movielibrary.utils.ModelMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService service;
    private final ModelMapper mapper;

    @Autowired
    public UserRestController(UserService service,
                              ModelMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<User> getAll(){
       return service.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id){
        try{
            return service.getById(id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public void create(@Valid @RequestBody UserRequestDto dto){

        User user = mapper.fromDto(dto);
        try {
            service.create(user);
        }catch (DuplicateEntityException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @Valid @RequestBody UpdateUserRequestDto dto){

        User user = mapper.fromDtoUpdate(dto);
        try {
            service.update(user, id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){

        try {
            service.delete(id);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
