package com.school.system.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GlobalSearchResultDto {
    private String category; // STUDENT, TEACHER, PARENT, CLASS, SUBJECT
    private Long id;
    private String title;
    private String subtitle;
    private String url;
}
