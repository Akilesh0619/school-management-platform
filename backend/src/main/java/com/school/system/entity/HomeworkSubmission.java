package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework_submissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HomeworkSubmission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "submission_text", columnDefinition = "TEXT")
    private String submissionText;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(length = 50)
    private String status = "SUBMITTED";

    private java.math.BigDecimal marks;

    @Column(length = 255)
    private String feedback;

    @PrePersist
    protected void onCreate() { submissionDate = LocalDateTime.now(); }
}
