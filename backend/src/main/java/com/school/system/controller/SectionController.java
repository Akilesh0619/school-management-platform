package com.school.system.controller;

import com.school.system.dto.SectionDto;
import com.school.system.service.SectionService;
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
@RequestMapping("/api/sections")
@RequiredArgsConstructor
@Tag(name = "Sections Module", description = "Endpoints for administering class section listings")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_SETTINGS')")
    @Operation(summary = "Create a new classroom section")
    public ResponseEntity<SectionDto> createSection(@Valid @RequestBody SectionDto dto) {
        SectionDto created = sectionService.createSection(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_SETTINGS')")
    @Operation(summary = "Update an existing section record")
    public ResponseEntity<SectionDto> updateSection(@PathVariable Long id, @Valid @RequestBody SectionDto dto) {
        SectionDto updated = sectionService.updateSection(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_SETTINGS')")
    @Operation(summary = "Delete a section record by ID")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get section details by ID")
    public ResponseEntity<SectionDto> getSectionById(@PathVariable Long id) {
        SectionDto sectionDto = sectionService.getSectionById(id);
        return ResponseEntity.ok(sectionDto);
    }

    @GetMapping
    @Operation(summary = "List all classroom sections")
    public ResponseEntity<List<SectionDto>> getAllSections() {
        List<SectionDto> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "List classroom sections of a specific class")
    public ResponseEntity<List<SectionDto>> getSectionsByClass(@PathVariable Long classId) {
        List<SectionDto> sections = sectionService.getSectionsByClass(classId);
        return ResponseEntity.ok(sections);
    }
}
