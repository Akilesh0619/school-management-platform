package com.school.system.controller;

import com.school.system.dto.FeePaymentDto;
import com.school.system.dto.FeesStructureDto;
import com.school.system.service.FeeService;
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
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@Tag(name = "Fees Module", description = "Fee structure management and payment processing")
public class FeeController {

    private final FeeService feeService;

    @PostMapping("/structure")
    @PreAuthorize("hasAuthority('WRITE_FEES')")
    @Operation(summary = "Create a fee structure for a class")
    public ResponseEntity<FeesStructureDto> createStructure(@Valid @RequestBody FeesStructureDto dto) {
        return new ResponseEntity<>(feeService.createStructure(dto), HttpStatus.CREATED);
    }

    @PutMapping("/structure/{id}")
    @PreAuthorize("hasAuthority('WRITE_FEES')")
    @Operation(summary = "Update an existing fee structure")
    public ResponseEntity<FeesStructureDto> updateStructure(@PathVariable Long id, @Valid @RequestBody FeesStructureDto dto) {
        return ResponseEntity.ok(feeService.updateStructure(id, dto));
    }

    @DeleteMapping("/structure/{id}")
    @PreAuthorize("hasAuthority('WRITE_FEES')")
    @Operation(summary = "Delete a fee structure")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        feeService.deleteStructure(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/structure")
    @PreAuthorize("hasAuthority('READ_FEES')")
    @Operation(summary = "Get all fee structures")
    public ResponseEntity<List<FeesStructureDto>> getAllStructures() {
        return ResponseEntity.ok(feeService.getAllStructures());
    }

    @GetMapping("/structure/class/{classId}")
    @PreAuthorize("hasAuthority('READ_FEES')")
    @Operation(summary = "Get fee structures for a class and academic year")
    public ResponseEntity<List<FeesStructureDto>> getStructuresByClass(
            @PathVariable Long classId, @RequestParam String academicYear) {
        return ResponseEntity.ok(feeService.getStructuresByClass(classId, academicYear));
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAuthority('WRITE_FEES')")
    @Operation(summary = "Process a fee payment and generate receipt")
    public ResponseEntity<FeePaymentDto> processPayment(@Valid @RequestBody FeePaymentDto dto) {
        return new ResponseEntity<>(feeService.processPayment(dto), HttpStatus.CREATED);
    }

    @GetMapping("/payments/student/{studentId}")
    @PreAuthorize("hasAuthority('READ_FEES')")
    @Operation(summary = "Get all fee payments for a student")
    public ResponseEntity<List<FeePaymentDto>> getPaymentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeService.getPaymentsByStudent(studentId));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAuthority('READ_FEES')")
    @Operation(summary = "Get all fee payments")
    public ResponseEntity<List<FeePaymentDto>> getAllPayments() {
        return ResponseEntity.ok(feeService.getAllPayments());
    }
}
