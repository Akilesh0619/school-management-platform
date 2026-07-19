package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hostels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hostel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type; // BOYS, GIRLS

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "warden_name", nullable = false, length = 100)
    private String wardenName;

    @Column(name = "warden_phone", nullable = false, length = 20)
    private String wardenPhone;
}
