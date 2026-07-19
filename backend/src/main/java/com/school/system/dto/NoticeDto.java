package com.school.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String attachmentPath;

    @NotBlank(message = "Target audience is required")
    private String targetAudience; // ALL, TEACHERS, STUDENTS, PARENTS

    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
