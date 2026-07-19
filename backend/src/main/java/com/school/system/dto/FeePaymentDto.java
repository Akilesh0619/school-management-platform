package com.school.system.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeePaymentDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long feesStructureId;
    private String feeType;
    private BigDecimal totalAmount;

    @NotNull(message = "Amount paid is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amountPaid;

    private BigDecimal discount;
    private BigDecimal fine;
    private LocalDateTime paymentDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String transactionId;
    private String status;
    private String receiptNo;
}
