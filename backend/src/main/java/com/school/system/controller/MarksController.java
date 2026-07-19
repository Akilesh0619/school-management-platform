package com.school.system.controller;

import com.school.system.dto.MarksDto;
import com.school.system.security.CustomUserDetails;
import com.school.system.service.MarksService;
import com.school.system.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
@RequiredArgsConstructor
@Tag(name = "Marks Module", description = "Record and retrieve exam marks with automatic grade calculation")
public class MarksController {

    private final MarksService marksService;
    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_MARKS')")
    @Operation(summary = "Save or update marks for a student in a subject/exam type")
    public ResponseEntity<MarksDto> saveMarks(
            @Valid @RequestBody MarksDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teacherId = null;
        try { teacherId = teacherService.getTeacherByUserId(userDetails.getId()).getId(); } catch (Exception ignored) {}
        return new ResponseEntity<>(marksService.saveMarks(dto, teacherId), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('READ_MARKS')")
    @Operation(summary = "Get all marks for a student")
    public ResponseEntity<List<MarksDto>> getMarksByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(marksService.getMarksByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/exam/{examType}")
    @PreAuthorize("hasAuthority('READ_MARKS')")
    @Operation(summary = "Get marks for a student filtered by exam type")
    public ResponseEntity<List<MarksDto>> getMarksByStudentAndExam(
            @PathVariable Long studentId, @PathVariable String examType) {
        return ResponseEntity.ok(marksService.getMarksByStudentAndExamType(studentId, examType));
    }
}
