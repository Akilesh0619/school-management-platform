package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportRoute {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_point", nullable = false, length = 150)
    private String startPoint;

    @Column(name = "end_point", nullable = false, length = 150)
    private String endPoint;

    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal fare;
}
