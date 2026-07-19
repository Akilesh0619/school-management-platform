package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "school_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName = "Enterprise Academy";

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "logo_path", length = 255)
    private String logoPath;

    @Column(length = 10)
    private String currency = "USD";

    @Column(name = "current_academic_year", length = 20)
    private String currentAcademicYear = "2026-2027";
}
