package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // e.g. "Grade 10", "Grade 11"

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear; // e.g. "2026-2027"

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(nullable = false)
    private Integer capacity;
}
