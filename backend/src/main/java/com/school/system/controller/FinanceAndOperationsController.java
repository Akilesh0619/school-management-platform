package com.school.system.controller;

import com.school.system.dto.FinanceAndLeaveDto.*;
import com.school.system.service.FinanceAndOperationsService;
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
@RequestMapping("/api/operations")
@RequiredArgsConstructor
@Tag(name = "Finance & Operations Module", description = "Endpoints for Finance Ledgers, Teacher Leave approvals, and Inventory Assets")
public class FinanceAndOperationsController {

    private final FinanceAndOperationsService service;

    // ---- Finance ----
    @PostMapping("/finance")
    @PreAuthorize("hasAuthority('WRITE_FINANCE')")
    @Operation(summary = "Record income or expense ledger entry")
    public ResponseEntity<LedgerDto> createLedger(@Valid @RequestBody LedgerDto dto) {
        return new ResponseEntity<>(service.createLedgerEntry(dto), HttpStatus.CREATED);
    }

    @GetMapping("/finance")
    @PreAuthorize("hasAuthority('READ_FINANCE')")
    @Operation(summary = "Get all income and expense ledgers")
    public ResponseEntity<List<LedgerDto>> getAllLedgers() {
        return ResponseEntity.ok(service.getAllLedgers());
    }

    // ---- Teacher Leaves ----
    @PostMapping("/leaves")
    @PreAuthorize("hasAuthority('WRITE_LEAVES')")
    @Operation(summary = "Submit a teacher leave request")
    public ResponseEntity<LeaveDto> applyLeave(@Valid @RequestBody LeaveDto dto) {
        return new ResponseEntity<>(service.applyLeave(dto), HttpStatus.CREATED);
    }

    @PutMapping("/leaves/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Approve or reject a teacher leave request")
    public ResponseEntity<LeaveDto> updateLeaveStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(service.updateLeaveStatus(id, status));
    }

    @GetMapping("/leaves")
    @PreAuthorize("hasAuthority('READ_LEAVES')")
    @Operation(summary = "Get all teacher leave applications")
    public ResponseEntity<List<LeaveDto>> getAllLeaves() {
        return ResponseEntity.ok(service.getAllLeaves());
    }

    // ---- Inventory ----
    @PostMapping("/inventory")
    @PreAuthorize("hasAuthority('WRITE_INVENTORY')")
    @Operation(summary = "Create or update inventory asset item")
    public ResponseEntity<InventoryDto> saveInventory(@Valid @RequestBody InventoryDto dto) {
        return new ResponseEntity<>(service.saveInventoryItem(dto), HttpStatus.CREATED);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAuthority('READ_INVENTORY')")
    @Operation(summary = "List all inventory asset items")
    public ResponseEntity<List<InventoryDto>> getAllInventory() {
        return ResponseEntity.ok(service.getAllInventory());
    }
}
