package com.school.system.service;

import com.school.system.dto.MarksDto;
import com.school.system.entity.Marks;
import com.school.system.entity.Student;
import com.school.system.entity.Subject;
import com.school.system.entity.Teacher;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.MarksRepository;
import com.school.system.repository.StudentRepository;
import com.school.system.repository.SubjectRepository;
import com.school.system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public MarksDto saveMarks(MarksDto dto, Long gradedByTeacherId) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + dto.getStudentId()));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + dto.getSubjectId()));

        if (dto.getMarksObtained().compareTo(dto.getMaxMarks()) > 0) {
            throw new BadRequestException("Marks obtained cannot exceed max marks");
        }

        // Upsert: update if exists, else create
        Marks marks = marksRepository.findByStudentIdAndSubjectIdAndExamType(
                dto.getStudentId(), dto.getSubjectId(), dto.getExamType())
                .orElse(new Marks());

        Teacher teacher = null;
        if (gradedByTeacherId != null) {
            teacher = teacherRepository.findByIdAndDeletedAtIsNull(gradedByTeacherId).orElse(null);
        }

        marks.setStudent(student);
        marks.setSubject(subject);
        marks.setExamType(dto.getExamType().toUpperCase());
        marks.setMarksObtained(dto.getMarksObtained());
        marks.setMaxMarks(dto.getMaxMarks());
        marks.setGrade(calculateGrade(dto.getMarksObtained(), dto.getMaxMarks()));
        marks.setRemarks(dto.getRemarks());
        marks.setGradedBy(teacher);

        Marks saved = marksRepository.save(marks);
        log.info("Marks saved for student {} - Subject {} - Exam {}", student.getName(), subject.getName(), dto.getExamType());
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MarksDto> getMarksByStudent(Long studentId) {
        return marksRepository.findByStudentId(studentId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MarksDto> getMarksByStudentAndExamType(Long studentId, String examType) {
        return marksRepository.findByStudentIdAndExamType(studentId, examType.toUpperCase()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    /** Calculates grade from percentage */
    private String calculateGrade(BigDecimal obtained, BigDecimal max) {
        if (max.compareTo(BigDecimal.ZERO) == 0) return "N/A";
        double pct = obtained.divide(max, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }

    private MarksDto mapToDto(Marks m) {
        double pct = m.getMaxMarks().compareTo(BigDecimal.ZERO) > 0
                ? m.getMarksObtained().divide(m.getMaxMarks(), 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;
        return MarksDto.builder()
                .id(m.getId())
                .studentId(m.getStudent().getId())
                .studentName(m.getStudent().getName())
                .admissionNumber(m.getStudent().getAdmissionNumber())
                .subjectId(m.getSubject().getId())
                .subjectName(m.getSubject().getName())
                .examType(m.getExamType())
                .marksObtained(m.getMarksObtained())
                .maxMarks(m.getMaxMarks())
                .grade(m.getGrade())
                .remarks(m.getRemarks())
                .gradedById(m.getGradedBy() != null ? m.getGradedBy().getId() : null)
                .gradedByName(m.getGradedBy() != null ? m.getGradedBy().getName() : null)
                .percentage(Math.round(pct * 100.0) / 100.0)
                .build();
    }
}
