package com.school.system.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDto {
    private long totalStudents;
    private long totalTeachers;
    private long totalParents;
    private long totalClasses;
    private long totalSubjects;
    private BigDecimal totalFeeCollected;
    private long studentsWithPendingFees;
    private long totalNotices;
    private long totalEvents;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private List<RecentActivityDto> recentActivities;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RecentActivityDto {
        private String username;
        private String action;
        private String details;
        private String timestamp;
    }
}
