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
public class SectionDto {
    private Long id;

    @NotBlank(message = "Section name is required")
    private String name;

    @NotNull(message = "Class ID is required")
    private Long classId;

    private String className;

    private Long classTeacherId;

    private String classTeacherName;
}
