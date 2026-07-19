package com.school.system.controller;

import com.school.system.dto.StudentCreateRequest;
import com.school.system.dto.StudentDto;
import com.school.system.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students Module", description = "CRUD operations for student records and class/parent associations")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_STUDENTS')")
    @Operation(summary = "Register a new student profile")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentCreateRequest request) {
        StudentDto created = studentService.createStudent(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_STUDENTS')")
    @Operation(summary = "Update an existing student profile by ID")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentCreateRequest request) {
        StudentDto updated = studentService.updateStudent(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_STUDENTS')")
    @Operation(summary = "Soft-delete a student profile and disable login")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_STUDENTS')")
    @Operation(summary = "Get student profile details by ID")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get student profile by user account ID")
    public ResponseEntity<StudentDto> getStudentByUserId(@PathVariable Long userId) {
        StudentDto student = studentService.getStudentByUserId(userId);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_STUDENTS')")
    @Operation(summary = "List all registered student profiles")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAuthority('READ_STUDENTS')")
    @Operation(summary = "List students enrolled in a specific class")
    public ResponseEntity<List<StudentDto>> getStudentsByClass(@PathVariable Long classId) {
        List<StudentDto> students = studentService.getStudentsByClass(classId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAuthority('READ_STUDENTS')")
    @Operation(summary = "List children linked to a parent profile")
    public ResponseEntity<List<StudentDto>> getStudentsByParent(@PathVariable Long parentId) {
        List<StudentDto> students = studentService.getStudentsByParent(parentId);
        return ResponseEntity.ok(students);
    }
}
