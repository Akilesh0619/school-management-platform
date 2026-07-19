package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "homework")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Homework {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "due_date", nullable = false)
    private java.time.LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Teacher createdBy;
}
