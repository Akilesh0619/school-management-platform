package com.school.system.controller;

import com.school.system.dto.ClassDto;
import com.school.system.service.ClassService;
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
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Classes Module", description = "Endpoints for administering standard classroom designations")
public class ClassController {

    private final ClassService classService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_SETTINGS')")
    @Operation(summary = "Create a new classroom grade")
    public ResponseEntity<ClassDto> createClass(@Valid @RequestBody ClassDto dto) {
        ClassDto created = classService.createClass(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_SETTINGS')")
    @Operation(summary = "Update an existing class record")
    public ResponseEntity<ClassDto> updateClass(@PathVariable Long id, @Valid @RequestBody ClassDto dto) {
        ClassDto updated = classService.updateClass(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_SETTINGS')")
    @Operation(summary = "Delete a class designation by ID")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a class record by ID")
    public ResponseEntity<ClassDto> getClassById(@PathVariable Long id) {
        ClassDto classDto = classService.getClassById(id);
        return ResponseEntity.ok(classDto);
    }

    @GetMapping
    @Operation(summary = "List all standard classes")
    public ResponseEntity<List<ClassDto>> getAllClasses() {
        List<ClassDto> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }
}
