package com.school.system.service;

import com.school.system.dto.FeePaymentDto;
import com.school.system.dto.FeesStructureDto;
import com.school.system.entity.*;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeService {

    private final FeesStructureRepository feesStructureRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;

    // ---- Fees Structure ----
    @Transactional
    public FeesStructureDto createStructure(FeesStructureDto dto) {
        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + dto.getClassId()));
        FeesStructure fs = FeesStructure.builder()
                .classEntity(classEntity)
                .academicYear(dto.getAcademicYear())
                .feeType(dto.getFeeType())
                .amount(dto.getAmount())
                .dueDate(dto.getDueDate())
                .build();
        return mapStructureToDto(feesStructureRepository.save(fs));
    }

    @Transactional
    public FeesStructureDto updateStructure(Long id, FeesStructureDto dto) {
        FeesStructure fs = feesStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found: " + id));
        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + dto.getClassId()));
        fs.setClassEntity(classEntity);
        fs.setAcademicYear(dto.getAcademicYear());
        fs.setFeeType(dto.getFeeType());
        fs.setAmount(dto.getAmount());
        fs.setDueDate(dto.getDueDate());
        return mapStructureToDto(feesStructureRepository.save(fs));
    }

    @Transactional
    public void deleteStructure(Long id) {
        if (!feesStructureRepository.existsById(id))
            throw new ResourceNotFoundException("Fee structure not found: " + id);
        feesStructureRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FeesStructureDto> getStructuresByClass(Long classId, String academicYear) {
        return feesStructureRepository.findByClassEntityIdAndAcademicYear(classId, academicYear)
                .stream().map(this::mapStructureToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeesStructureDto> getAllStructures() {
        return feesStructureRepository.findAll().stream()
                .map(this::mapStructureToDto).collect(Collectors.toList());
    }

    // ---- Payments ----
    @Transactional
    public FeePaymentDto processPayment(FeePaymentDto dto) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + dto.getStudentId()));
        FeesStructure fs = feesStructureRepository.findById(dto.getFeesStructureId())
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found: " + dto.getFeesStructureId()));

        BigDecimal discount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
        BigDecimal fine = dto.getFine() != null ? dto.getFine() : BigDecimal.ZERO;
        BigDecimal totalDue = fs.getAmount().subtract(discount).add(fine);
        String status = dto.getAmountPaid().compareTo(totalDue) >= 0 ? "PAID" : "PARTIAL";

        String receiptNo = "RCPT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        FeePayment payment = FeePayment.builder()
                .student(student)
                .feesStructure(fs)
                .amountPaid(dto.getAmountPaid())
                .discount(discount)
                .fine(fine)
                .paymentMethod(dto.getPaymentMethod())
                .transactionId(dto.getTransactionId())
                .status(status)
                .receiptNo(receiptNo)
                .build();

        FeePayment saved = feePaymentRepository.save(payment);
        log.info("Fee payment processed for student {}: Receipt {}", student.getName(), receiptNo);
        return mapPaymentToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<FeePaymentDto> getPaymentsByStudent(Long studentId) {
        return feePaymentRepository.findByStudentId(studentId).stream()
                .map(this::mapPaymentToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeePaymentDto> getAllPayments() {
        return feePaymentRepository.findAll().stream()
                .map(this::mapPaymentToDto).collect(Collectors.toList());
    }

    private FeesStructureDto mapStructureToDto(FeesStructure fs) {
        return FeesStructureDto.builder()
                .id(fs.getId())
                .classId(fs.getClassEntity().getId())
                .className(fs.getClassEntity().getName())
                .academicYear(fs.getAcademicYear())
                .feeType(fs.getFeeType())
                .amount(fs.getAmount())
                .dueDate(fs.getDueDate())
                .build();
    }

    private FeePaymentDto mapPaymentToDto(FeePayment fp) {
        return FeePaymentDto.builder()
                .id(fp.getId())
                .studentId(fp.getStudent().getId())
                .studentName(fp.getStudent().getName())
                .admissionNumber(fp.getStudent().getAdmissionNumber())
                .feesStructureId(fp.getFeesStructure().getId())
                .feeType(fp.getFeesStructure().getFeeType())
                .totalAmount(fp.getFeesStructure().getAmount())
                .amountPaid(fp.getAmountPaid())
                .discount(fp.getDiscount())
                .fine(fp.getFine())
                .paymentDate(fp.getPaymentDate())
                .paymentMethod(fp.getPaymentMethod())
                .transactionId(fp.getTransactionId())
                .status(fp.getStatus())
                .receiptNo(fp.getReceiptNo())
                .build();
    }
}
