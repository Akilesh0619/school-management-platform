package com.school.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeePayment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fees_structure_id", nullable = false)
    private FeesStructure feesStructure;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal fine = BigDecimal.ZERO;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // CASH, UPI, CARD, NET_BANKING

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(nullable = false, length = 50)
    private String status; // PAID, PARTIAL, UNPAID

    @Column(name = "receipt_no", nullable = false, unique = true, length = 100)
    private String receiptNo;

    @PrePersist
    protected void onCreate() { if (paymentDate == null) paymentDate = LocalDateTime.now(); }
}
