package com.school.system.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeesStructureDto {
    private Long id;

    @NotNull(message = "Class ID is required")
    private Long classId;
    private String className;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotBlank(message = "Fee type is required")
    private String feeType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
}
