package com.example.movielibrary.models.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDto {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Size(min = 8, message = "Password must be at least 8 characters long!")
    private String password;

    public UpdateUserRequestDto(){}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
