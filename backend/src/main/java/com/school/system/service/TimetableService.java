package com.school.system.service;

import com.school.system.dto.TimetableDto;
import com.school.system.entity.*;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public TimetableDto createEntry(TimetableDto dto) {
        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + dto.getClassId()));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + dto.getSubjectId()));
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + dto.getTeacherId()));

        // Conflict detection
        checkConflicts(dto.getTeacherId(), dto.getClassId(), dto.getRoomNumber(),
                dto.getDayOfWeek(), dto.getStartTime().toString(), dto.getEndTime().toString(), null);

        Timetable entry = Timetable.builder()
                .classEntity(classEntity)
                .subject(subject)
                .teacher(teacher)
                .dayOfWeek(dto.getDayOfWeek().toUpperCase())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .roomNumber(dto.getRoomNumber())
                .build();

        Timetable saved = timetableRepository.save(entry);
        log.info("Timetable entry created for class {} on {}", classEntity.getName(), dto.getDayOfWeek());
        return mapToDto(saved);
    }

    @Transactional
    public TimetableDto updateEntry(Long id, TimetableDto dto) {
        Timetable entry = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found: " + id));

        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + dto.getClassId()));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + dto.getSubjectId()));
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + dto.getTeacherId()));

        checkConflicts(dto.getTeacherId(), dto.getClassId(), dto.getRoomNumber(),
                dto.getDayOfWeek(), dto.getStartTime().toString(), dto.getEndTime().toString(), id);

        entry.setClassEntity(classEntity);
        entry.setSubject(subject);
        entry.setTeacher(teacher);
        entry.setDayOfWeek(dto.getDayOfWeek().toUpperCase());
        entry.setStartTime(dto.getStartTime());
        entry.setEndTime(dto.getEndTime());
        entry.setRoomNumber(dto.getRoomNumber());

        return mapToDto(timetableRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(Long id) {
        if (!timetableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Timetable entry not found: " + id);
        }
        timetableRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TimetableDto> getTimetableByClass(Long classId) {
        return timetableRepository.findByClassEntityId(classId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimetableDto> getTimetableByTeacher(Long teacherId) {
        return timetableRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimetableDto> getAllEntries() {
        return timetableRepository.findAll().stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    private void checkConflicts(Long teacherId, Long classId, String room, String day,
                                 String startStr, String endStr, Long excludeId) {
        java.time.LocalTime start = java.time.LocalTime.parse(startStr);
        java.time.LocalTime end = java.time.LocalTime.parse(endStr);

        if (timetableRepository.existsTeacherConflict(teacherId, day.toUpperCase(), start, end, excludeId)) {
            throw new BadRequestException("Teacher has a scheduling conflict on " + day + " between " + startStr + " and " + endStr);
        }
        if (timetableRepository.existsRoomConflict(room, day.toUpperCase(), start, end, excludeId)) {
            throw new BadRequestException("Room " + room + " is already booked on " + day + " between " + startStr + " and " + endStr);
        }
        if (timetableRepository.existsClassConflict(classId, day.toUpperCase(), start, end, excludeId)) {
            throw new BadRequestException("This class already has a period scheduled on " + day + " between " + startStr + " and " + endStr);
        }
    }

    private TimetableDto mapToDto(Timetable t) {
        return TimetableDto.builder()
                .id(t.getId())
                .classId(t.getClassEntity().getId())
                .className(t.getClassEntity().getName())
                .subjectId(t.getSubject().getId())
                .subjectName(t.getSubject().getName())
                .teacherId(t.getTeacher().getId())
                .teacherName(t.getTeacher().getName())
                .dayOfWeek(t.getDayOfWeek())
                .startTime(t.getStartTime())
                .endTime(t.getEndTime())
                .roomNumber(t.getRoomNumber())
                .build();
    }
}
