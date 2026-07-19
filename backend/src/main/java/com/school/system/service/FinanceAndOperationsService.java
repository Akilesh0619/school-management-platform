package com.school.system.service;

import com.school.system.dto.FinanceAndLeaveDto.*;
import com.school.system.entity.FinanceLedger;
import com.school.system.entity.InventoryItem;
import com.school.system.entity.Teacher;
import com.school.system.entity.TeacherLeave;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.FinanceLedgerRepository;
import com.school.system.repository.InventoryRepository;
import com.school.system.repository.TeacherLeaveRepository;
import com.school.system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceAndOperationsService {

    private final FinanceLedgerRepository financeLedgerRepository;
    private final TeacherLeaveRepository teacherLeaveRepository;
    private final InventoryRepository inventoryRepository;
    private final TeacherRepository teacherRepository;

    // ---- Finance ----
    @Transactional
    public LedgerDto createLedgerEntry(LedgerDto dto) {
        String ref = dto.getReferenceNo() != null ? dto.getReferenceNo() : "TXN-" + System.currentTimeMillis();
        FinanceLedger ledger = FinanceLedger.builder()
                .type(dto.getType().toUpperCase())
                .category(dto.getCategory())
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .description(dto.getDescription())
                .referenceNo(ref)
                .build();
        FinanceLedger saved = financeLedgerRepository.save(ledger);
        return LedgerDto.builder()
                .id(saved.getId())
                .type(saved.getType())
                .category(saved.getCategory())
                .amount(saved.getAmount())
                .transactionDate(saved.getTransactionDate())
                .description(saved.getDescription())
                .referenceNo(saved.getReferenceNo())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LedgerDto> getAllLedgers() {
        return financeLedgerRepository.findAllByOrderByTransactionDateDesc().stream()
                .map(l -> LedgerDto.builder()
                        .id(l.getId())
                        .type(l.getType())
                        .category(l.getCategory())
                        .amount(l.getAmount())
                        .transactionDate(l.getTransactionDate())
                        .description(l.getDescription())
                        .referenceNo(l.getReferenceNo())
                        .build())
                .collect(Collectors.toList());
    }

    // ---- Teacher Leaves ----
    @Transactional
    public LeaveDto applyLeave(LeaveDto dto) {
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + dto.getTeacherId()));
        TeacherLeave leave = TeacherLeave.builder()
                .teacher(teacher)
                .leaveType(dto.getLeaveType().toUpperCase())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status("PENDING")
                .build();
        TeacherLeave saved = teacherLeaveRepository.save(leave);
        return mapLeaveToDto(saved);
    }

    @Transactional
    public LeaveDto updateLeaveStatus(Long leaveId, String status) {
        TeacherLeave leave = teacherLeaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
        leave.setStatus(status.toUpperCase());
        return mapLeaveToDto(teacherLeaveRepository.save(leave));
    }

    @Transactional(readOnly = true)
    public List<LeaveDto> getAllLeaves() {
        return teacherLeaveRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapLeaveToDto).collect(Collectors.toList());
    }

    // ---- Inventory Assets ----
    @Transactional
    public InventoryDto saveInventoryItem(InventoryDto dto) {
        String sku = dto.getSku() != null ? dto.getSku() : "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String status = dto.getQuantity() > 10 ? "IN_STOCK" : dto.getQuantity() > 0 ? "LOW_STOCK" : "OUT_OF_STOCK";

        InventoryItem item;
        if (dto.getId() != null) {
            item = inventoryRepository.findById(dto.getId()).orElse(new InventoryItem());
        } else {
            item = new InventoryItem();
        }

        item.setName(dto.getName());
        item.setSku(sku);
        item.setCategory(dto.getCategory());
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setLocation(dto.getLocation());
        item.setStatus(status);

        InventoryItem saved = inventoryRepository.save(item);
        return InventoryDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .sku(saved.getSku())
                .category(saved.getCategory())
                .quantity(saved.getQuantity())
                .unit(saved.getUnit())
                .location(saved.getLocation())
                .status(saved.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(i -> InventoryDto.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .sku(i.getSku())
                        .category(i.getCategory())
                        .quantity(i.getQuantity())
                        .unit(i.getUnit())
                        .location(i.getLocation())
                        .status(i.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    private LeaveDto mapLeaveToDto(TeacherLeave l) {
        return LeaveDto.builder()
                .id(l.getId())
                .teacherId(l.getTeacher().getId())
                .teacherName(l.getTeacher().getName())
                .leaveType(l.getLeaveType())
                .startDate(l.getStartDate())
                .endDate(l.getEndDate())
                .reason(l.getReason())
                .status(l.getStatus())
                .build();
    }
}
