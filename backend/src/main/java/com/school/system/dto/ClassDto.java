package com.school.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDto {
    private Long id;

    @NotBlank(message = "Class name is required")
    private String name;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    private String roomNumber;

    @NotNull(message = "Capacity is required")
    private Integer capacity;
}
