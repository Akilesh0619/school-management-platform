package com.school.system.controller;

import com.school.system.dto.TimetableDto;
import com.school.system.service.TimetableService;
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
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@Tag(name = "Timetable Module", description = "Weekly schedule management with conflict detection")
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_TIMETABLE')")
    @Operation(summary = "Create a new timetable entry with conflict detection")
    public ResponseEntity<TimetableDto> createEntry(@Valid @RequestBody TimetableDto dto) {
        return new ResponseEntity<>(timetableService.createEntry(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_TIMETABLE')")
    @Operation(summary = "Update a timetable entry")
    public ResponseEntity<TimetableDto> updateEntry(@PathVariable Long id, @Valid @RequestBody TimetableDto dto) {
        return ResponseEntity.ok(timetableService.updateEntry(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_TIMETABLE')")
    @Operation(summary = "Delete a timetable entry")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        timetableService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get full weekly timetable for a class")
    public ResponseEntity<List<TimetableDto>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(timetableService.getTimetableByClass(classId));
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get full weekly timetable for a teacher")
    public ResponseEntity<List<TimetableDto>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(timetableService.getTimetableByTeacher(teacherId));
    }

    @GetMapping
    @Operation(summary = "Get all timetable entries")
    public ResponseEntity<List<TimetableDto>> getAll() {
        return ResponseEntity.ok(timetableService.getAllEntries());
    }
}
