package com.school.system.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceSummaryDto {
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private long totalDays;
    private long presentDays;
    private long absentDays;
    private long lateDays;
    private long leaveDays;
    private double attendancePercentage;
}
