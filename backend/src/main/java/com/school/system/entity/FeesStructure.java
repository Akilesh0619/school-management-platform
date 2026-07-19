package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fees_structure")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeesStructure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "fee_type", nullable = false, length = 100)
    private String feeType; // TUITION, EXAM, LIBRARY, TRANSPORT, HOSTEL, MISC

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
}
