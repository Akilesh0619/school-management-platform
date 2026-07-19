package com.school.system.service;

import com.school.system.dto.SectionDto;
import com.school.system.entity.ClassEntity;
import com.school.system.entity.Section;
import com.school.system.entity.Teacher;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.ClassRepository;
import com.school.system.repository.SectionRepository;
import com.school.system.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public SectionDto createSection(SectionDto dto) {
        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));

        if (sectionRepository.findByNameAndClassEntityId(dto.getName(), dto.getClassId()).isPresent()) {
            throw new BadRequestException("Section " + dto.getName() + " already exists in class " + classEntity.getName());
        }

        Teacher teacher = null;
        if (dto.getClassTeacherId() != null) {
            teacher = teacherRepository.findByIdAndDeletedAtIsNull(dto.getClassTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getClassTeacherId()));
        }

        Section section = Section.builder()
                .name(dto.getName())
                .classEntity(classEntity)
                .classTeacher(teacher)
                .build();

        Section saved = sectionRepository.save(section);
        log.info("Section {} created for class {}", saved.getName(), classEntity.getName());
        return mapToDto(saved);
    }

    @Transactional
    public SectionDto updateSection(Long id, SectionDto dto) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));

        ClassEntity classEntity = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + dto.getClassId()));

        if (!section.getName().equals(dto.getName()) && sectionRepository.findByNameAndClassEntityId(dto.getName(), dto.getClassId()).isPresent()) {
            throw new BadRequestException("Section " + dto.getName() + " already exists in class " + classEntity.getName());
        }

        Teacher teacher = null;
        if (dto.getClassTeacherId() != null) {
            teacher = teacherRepository.findByIdAndDeletedAtIsNull(dto.getClassTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getClassTeacherId()));
        }

        section.setName(dto.getName());
        section.setClassEntity(classEntity);
        section.setClassTeacher(teacher);

        Section updated = sectionRepository.save(section);
        log.info("Section {} updated", updated.getName());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteSection(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
        sectionRepository.delete(section);
        log.info("Section {} deleted", section.getName());
    }

    @Transactional(readOnly = true)
    public SectionDto getSectionById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + id));
        return mapToDto(section);
    }

    @Transactional(readOnly = true)
    public List<SectionDto> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SectionDto> getSectionsByClass(Long classId) {
        return sectionRepository.findByClassEntityId(classId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SectionDto mapToDto(Section section) {
        return SectionDto.builder()
                .id(section.getId())
                .name(section.getName())
                .classId(section.getClassEntity().getId())
                .className(section.getClassEntity().getName())
                .classTeacherId(section.getClassTeacher() != null ? section.getClassTeacher().getId() : null)
                .classTeacherName(section.getClassTeacher() != null ? section.getClassTeacher().getName() : null)
                .build();
    }
}
