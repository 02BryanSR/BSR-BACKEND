package com.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CustomerUpdateDTO {

    @Size(max = 120, message = "Name cannot exceed 120 characters")
    private String name;

    @Size(max = 120, message = "Last name cannot exceed 120 characters")
    private String lastName;

    @Email(message = "Email format is invalid")
    private String email;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @PositiveOrZero(message = "Number must be >= 0")
    private Long number;

    private String role;
    private Boolean enabled;

    public CustomerUpdateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
