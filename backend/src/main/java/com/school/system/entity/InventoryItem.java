package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(length = 100)
    private String location;

    @Column(nullable = false, length = 50)
    private String status; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist @PreUpdate
    protected void onUpdate() { lastUpdated = LocalDateTime.now(); }
}
