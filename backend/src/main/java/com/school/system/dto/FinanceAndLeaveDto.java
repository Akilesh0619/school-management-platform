package com.school.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinanceAndLeaveDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LedgerDto {
        private Long id;
        private String type; // INCOME, EXPENSE
        private String category;
        private BigDecimal amount;
        private LocalDate transactionDate;
        private String description;
        private String referenceNo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LeaveDto {
        private Long id;
        private Long teacherId;
        private String teacherName;
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private String status; // PENDING, APPROVED, REJECTED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InventoryDto {
        private Long id;
        private String name;
        private String sku;
        private String category;
        private Integer quantity;
        private String unit;
        private String location;
        private String status; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    }
}
