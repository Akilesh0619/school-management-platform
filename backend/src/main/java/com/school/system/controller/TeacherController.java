package com.school.system.controller;

import com.school.system.dto.TeacherCreateRequest;
import com.school.system.dto.TeacherDto;
import com.school.system.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers Module", description = "CRUD operations for teacher accounts and department metrics")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_TEACHERS')")
    @Operation(summary = "Register a new teacher profile")
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherCreateRequest request) {
        TeacherDto created = teacherService.createTeacher(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_TEACHERS')")
    @Operation(summary = "Update teacher profile details by ID")
    public ResponseEntity<TeacherDto> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherCreateRequest request) {
        TeacherDto updated = teacherService.updateTeacher(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_TEACHERS')")
    @Operation(summary = "Soft-delete a teacher profile")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_TEACHERS')")
    @Operation(summary = "Get teacher profile details by ID")
    public ResponseEntity<TeacherDto> getTeacherById(@PathVariable Long id) {
        TeacherDto teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'TEACHER')")
    @Operation(summary = "Get teacher profile details by user account ID")
    public ResponseEntity<TeacherDto> getTeacherByUserId(@PathVariable Long userId) {
        TeacherDto teacher = teacherService.getTeacherByUserId(userId);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_TEACHERS')")
    @Operation(summary = "List all registered teacher profiles")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        List<TeacherDto> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }
}
