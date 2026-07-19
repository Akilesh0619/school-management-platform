package com.school.system.service;

import com.school.system.dto.ClassDto;
import com.school.system.entity.ClassEntity;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService {

    private final ClassRepository classRepository;

    @Transactional
    public ClassDto createClass(ClassDto dto) {
        if (classRepository.findByNameAndAcademicYear(dto.getName(), dto.getAcademicYear()).isPresent()) {
            throw new BadRequestException("Class with name " + dto.getName() + " already exists for academic year " + dto.getAcademicYear());
        }

        ClassEntity entity = ClassEntity.builder()
                .name(dto.getName())
                .academicYear(dto.getAcademicYear())
                .roomNumber(dto.getRoomNumber())
                .capacity(dto.getCapacity())
                .build();

        ClassEntity saved = classRepository.save(entity);
        log.info("Class created successfully: {}, Year: {}", saved.getName(), saved.getAcademicYear());
        return mapToDto(saved);
    }

    @Transactional
    public ClassDto updateClass(Long id, ClassDto dto) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));

        // Check uniqueness if name or academic year is changing
        if ((!entity.getName().equals(dto.getName()) || !entity.getAcademicYear().equals(dto.getAcademicYear()))
                && classRepository.findByNameAndAcademicYear(dto.getName(), dto.getAcademicYear()).isPresent()) {
            throw new BadRequestException("Class with name " + dto.getName() + " already exists for academic year " + dto.getAcademicYear());
        }

        entity.setName(dto.getName());
        entity.setAcademicYear(dto.getAcademicYear());
        entity.setRoomNumber(dto.getRoomNumber());
        entity.setCapacity(dto.getCapacity());

        ClassEntity updated = classRepository.save(entity);
        log.info("Class updated successfully: {}", updated.getName());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteClass(Long id) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
        classRepository.delete(entity);
        log.info("Class deleted successfully: {}", entity.getName());
    }

    @Transactional(readOnly = true)
    public ClassDto getClassById(Long id) {
        ClassEntity entity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + id));
        return mapToDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ClassDto> getAllClasses() {
        return classRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ClassDto mapToDto(ClassEntity entity) {
        return ClassDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .academicYear(entity.getAcademicYear())
                .roomNumber(entity.getRoomNumber())
                .capacity(entity.getCapacity())
                .build();
    }
}
