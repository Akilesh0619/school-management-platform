package com.school.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String occupation;

    @NotBlank(message = "Relation is required")
    private String relation; // FATHER, MOTHER, GUARDIAN

    @NotBlank(message = "Address is required")
    private String address;
}
