package com.school.system.controller;

import com.school.system.dto.AttendanceDto;
import com.school.system.dto.AttendanceSummaryDto;
import com.school.system.security.CustomUserDetails;
import com.school.system.service.AttendanceService;
import com.school.system.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Module", description = "Mark, update and report student attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_ATTENDANCE')")
    @Operation(summary = "Mark attendance for a student on a specific date")
    public ResponseEntity<AttendanceDto> markAttendance(
            @Valid @RequestBody AttendanceDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teacherId = null;
        try {
            teacherId = teacherService.getTeacherByUserId(userDetails.getId()).getId();
        } catch (Exception ignored) {}
        return new ResponseEntity<>(attendanceService.markAttendance(dto, teacherId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_ATTENDANCE')")
    @Operation(summary = "Update an attendance record")
    public ResponseEntity<AttendanceDto> updateAttendance(@PathVariable Long id, @Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, dto));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('READ_ATTENDANCE')")
    @Operation(summary = "Get attendance for a student between two dates")
    public ResponseEntity<List<AttendanceDto>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentAndDateRange(studentId, from, to));
    }

    @GetMapping("/class/{classId}/date/{date}")
    @PreAuthorize("hasAuthority('READ_ATTENDANCE')")
    @Operation(summary = "Get full class attendance for a specific date")
    public ResponseEntity<List<AttendanceDto>> getClassAttendance(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getClassAttendanceByDate(classId, date));
    }

    @GetMapping("/summary/{studentId}")
    @PreAuthorize("hasAuthority('READ_ATTENDANCE')")
    @Operation(summary = "Get attendance summary statistics for a student")
    public ResponseEntity<AttendanceSummaryDto> getAttendanceSummary(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceSummary(studentId));
    }
}
