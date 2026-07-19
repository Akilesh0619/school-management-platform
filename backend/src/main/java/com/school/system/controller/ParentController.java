package com.school.system.controller;

import com.school.system.dto.ParentCreateRequest;
import com.school.system.dto.ParentDto;
import com.school.system.service.ParentService;
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
@RequestMapping("/api/parents")
@RequiredArgsConstructor
@Tag(name = "Parents Module", description = "CRUD operations for parent profiles and student guardians links")
public class ParentController {

    private final ParentService parentService;

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_PARENTS')")
    @Operation(summary = "Register a new parent profile")
    public ResponseEntity<ParentDto> createParent(@Valid @RequestBody ParentCreateRequest request) {
        ParentDto created = parentService.createParent(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_PARENTS')")
    @Operation(summary = "Update parent details by ID")
    public ResponseEntity<ParentDto> updateParent(@PathVariable Long id, @Valid @RequestBody ParentCreateRequest request) {
        ParentDto updated = parentService.updateParent(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PARENTS')")
    @Operation(summary = "Soft-delete a parent profile")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PARENTS')")
    @Operation(summary = "Get parent details by ID")
    public ResponseEntity<ParentDto> getParentById(@PathVariable Long id) {
        ParentDto parent = parentService.getParentById(id);
        return ResponseEntity.ok(parent);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'TEACHER', 'PARENT')")
    @Operation(summary = "Get parent details by user account ID")
    public ResponseEntity<ParentDto> getParentByUserId(@PathVariable Long userId) {
        ParentDto parent = parentService.getParentByUserId(userId);
        return ResponseEntity.ok(parent);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_PARENTS')")
    @Operation(summary = "List all registered parent profiles")
    public ResponseEntity<List<ParentDto>> getAllParents() {
        List<ParentDto> parents = parentService.getAllParents();
        return ResponseEntity.ok(parents);
    }
}
