package com.school.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarksDto {
    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;
    private String studentName;
    private String admissionNumber;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    private String subjectName;

    @NotBlank(message = "Exam type is required")
    private String examType;

    @NotNull(message = "Marks obtained is required")
    private BigDecimal marksObtained;

    @NotNull(message = "Max marks is required")
    private BigDecimal maxMarks;

    private String grade;
    private String remarks;
    private Long gradedById;
    private String gradedByName;
    private double percentage;
}
