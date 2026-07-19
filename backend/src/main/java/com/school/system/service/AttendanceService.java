package com.school.system.service;

import com.school.system.dto.AttendanceDto;
import com.school.system.dto.AttendanceSummaryDto;
import com.school.system.entity.Attendance;
import com.school.system.entity.Student;
import com.school.system.entity.Teacher;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.AttendanceRepository;
import com.school.system.repository.StudentRepository;
import com.school.system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public AttendanceDto markAttendance(AttendanceDto dto, Long markedByTeacherId) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + dto.getStudentId()));

        // Prevent duplicate for same day
        if (attendanceRepository.findByStudentIdAndDate(dto.getStudentId(), dto.getDate()).isPresent()) {
            throw new BadRequestException("Attendance already marked for student " + student.getName() + " on " + dto.getDate());
        }

        Teacher teacher = null;
        if (markedByTeacherId != null) {
            teacher = teacherRepository.findByIdAndDeletedAtIsNull(markedByTeacherId).orElse(null);
        }

        Attendance attendance = Attendance.builder()
                .student(student)
                .date(dto.getDate())
                .status(dto.getStatus().toUpperCase())
                .remarks(dto.getRemarks())
                .markedBy(teacher)
                .build();

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance marked: {} - {} on {}", student.getName(), dto.getStatus(), dto.getDate());
        return mapToDto(saved);
    }

    @Transactional
    public AttendanceDto updateAttendance(Long id, AttendanceDto dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));
        attendance.setStatus(dto.getStatus().toUpperCase());
        attendance.setRemarks(dto.getRemarks());
        return mapToDto(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByStudentAndDateRange(Long studentId, LocalDate from, LocalDate to) {
        return attendanceRepository.findByStudentIdAndDateBetween(studentId, from, to)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto> getClassAttendanceByDate(Long classId, LocalDate date) {
        return attendanceRepository.findByStudentClassEntityIdAndDate(classId, date)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryDto getAttendanceSummary(Long studentId) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        long total = attendanceRepository.countByStudentId(studentId);
        long present = attendanceRepository.countByStudentIdAndStatus(studentId, "PRESENT");
        long absent = attendanceRepository.countByStudentIdAndStatus(studentId, "ABSENT");
        long late = attendanceRepository.countByStudentIdAndStatus(studentId, "LATE");
        long leave = attendanceRepository.countByStudentIdAndStatus(studentId, "LEAVE");
        double pct = total > 0 ? (double)(present + late) / total * 100 : 0;

        return AttendanceSummaryDto.builder()
                .studentId(studentId)
                .studentName(student.getName())
                .admissionNumber(student.getAdmissionNumber())
                .totalDays(total)
                .presentDays(present)
                .absentDays(absent)
                .lateDays(late)
                .leaveDays(leave)
                .attendancePercentage(Math.round(pct * 100.0) / 100.0)
                .build();
    }

    private AttendanceDto mapToDto(Attendance a) {
        return AttendanceDto.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getName())
                .admissionNumber(a.getStudent().getAdmissionNumber())
                .date(a.getDate())
                .status(a.getStatus())
                .remarks(a.getRemarks())
                .markedById(a.getMarkedBy() != null ? a.getMarkedBy().getId() : null)
                .markedByName(a.getMarkedBy() != null ? a.getMarkedBy().getName() : null)
                .build();
    }
}
