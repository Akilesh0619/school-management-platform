package com.school.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class StudentCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    private String bloodGroup;
    private String religion;
    private String category;
    
    @NotNull(message = "Class ID is required")
    private Long classId;

    private Long sectionId;
    private Long parentId;
    
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String medicalInfo;
    private String emergencyContact;

    @NotBlank(message = "Academic year is required")
    private String academicYear;
}
