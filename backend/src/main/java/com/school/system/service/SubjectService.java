package com.school.system.service;

import com.school.system.dto.SubjectDto;
import com.school.system.entity.Subject;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.findByCode(dto.getCode()).isPresent()) {
            throw new BadRequestException("Subject with code " + dto.getCode() + " already exists.");
        }

        Subject subject = Subject.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .credits(dto.getCredits())
                .department(dto.getDepartment())
                .build();

        Subject saved = subjectRepository.save(subject);
        log.info("Subject created successfully: {} ({})", saved.getName(), saved.getCode());
        return mapToDto(saved);
    }

    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // Check code uniqueness if changing code
        if (!subject.getCode().equals(dto.getCode()) && subjectRepository.findByCode(dto.getCode()).isPresent()) {
            throw new BadRequestException("Subject with code " + dto.getCode() + " already exists.");
        }

        subject.setName(dto.getName());
        subject.setCode(dto.getCode());
        subject.setCredits(dto.getCredits());
        subject.setDepartment(dto.getDepartment());

        Subject updated = subjectRepository.save(subject);
        log.info("Subject updated successfully: {}", updated.getName());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        subjectRepository.delete(subject);
        log.info("Subject deleted: {}", subject.getName());
    }

    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
        return mapToDto(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SubjectDto mapToDto(Subject subject) {
        return SubjectDto.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .credits(subject.getCredits())
                .department(subject.getDepartment())
                .build();
    }
}
