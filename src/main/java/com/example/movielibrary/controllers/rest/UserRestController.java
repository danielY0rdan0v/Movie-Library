package com.example.movielibrary.controllers.rest;

import com.example.movielibrary.models.user.UpdateUserRequestDto;
import com.example.movielibrary.models.user.User;
import com.example.movielibrary.models.user.UserRequestDto;
import com.example.movielibrary.models.user.UserResponseDto;
import com.example.movielibrary.services.UserService;
import com.example.movielibrary.utils.ModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Manage Users", description = "CRUD operations for Users")
public class UserRestController {

    private final UserService service;
    private final ModelMapper mapper;

    @Autowired
    public UserRestController(UserService service,
                              ModelMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @SecurityRequirement(name = "basicAuth")
    @GetMapping()
    @Operation(summary = "Returns all Users")
    public List<UserResponseDto> getAll(){
       return service.getAll();
    }


    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    @Operation(summary = "Returns a User by Id")
    public UserResponseDto getById(@PathVariable int id){
            return mapper.toDto(service.getById(id));
    }

    @PostMapping()
    @Operation(summary = "Creates a User")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto dto){

        User user = mapper.fromDto(dto);
        UserResponseDto dtoToReturn = mapper.toDto(service.create(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoToReturn);
    }


    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}")
    @Operation(summary = "Updates a User by Id")
    public ResponseEntity<UserResponseDto> update(@PathVariable int id, @Valid @RequestBody UpdateUserRequestDto dto){

        User user = mapper.fromDtoUpdate(dto);
        User updated = service.update(user, id);
        UserResponseDto updatedDto = mapper.toDto(updated);
        return ResponseEntity.ok(updatedDto);
    }

    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a User by Id")
    public ResponseEntity<Void> delete(@PathVariable int id){

            service.delete(id);
            return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}/promotion")
    @Operation(summary = "Make a user Admin")
    public ResponseEntity<Void> makeAdmin(@PathVariable int id){

            service.makeAdmin(id);
            return ResponseEntity.ok().build();
    }
}
