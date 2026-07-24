package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.exceptions.DuplicateEntityException;
import com.example.movielibrary.exceptions.EntityNotFoundException;
import com.example.movielibrary.models.user.UpdateUserRequestDto;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserRequestDto;
import com.example.movielibrary.models.user.UserResponseDto;
import com.example.movielibrary.services.UserService;
import com.example.movielibrary.utils.ModelMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserRestController {

    private final UserService service;
    private final ModelMapper mapper;

    @Autowired
    public UserRestController(UserService service,
                              ModelMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAll(){
       return service.getAll();
    }

    @GetMapping("/users/{id}")
    public UserResponseDto getById(@PathVariable int id){
        try{
            return mapper.toDto(service.getById(id));

        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto dto){

        User user = mapper.fromDto(dto);
        try {
            UserResponseDto dtoToReturn = mapper.toDto(service.create(user));
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoToReturn);

        }catch (DuplicateEntityException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable int id, @Valid @RequestBody UpdateUserRequestDto dto){

        User user = mapper.fromDtoUpdate(dto);
        try {
            User updated = service.update(user, id);
            UserResponseDto updatedDto = mapper.toDto(updated);
            return ResponseEntity.ok(updatedDto);
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){

        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
