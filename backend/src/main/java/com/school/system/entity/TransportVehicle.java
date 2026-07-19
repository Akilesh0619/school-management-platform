package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportVehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @Column(name = "driver_phone", nullable = false, length = 20)
    private String driverPhone;

    @Column(name = "driver_license", nullable = false, length = 50)
    private String driverLicense;
}
