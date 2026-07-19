package com.school.system.service;

import com.school.system.dto.GlobalSearchResultDto;
import com.school.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<GlobalSearchResultDto> searchEverywhere(String query) {
        List<GlobalSearchResultDto> results = new ArrayList<>();
        if (query == null || query.trim().length() < 2) {
            return results;
        }

        String q = query.trim().toLowerCase();

        // Search Students
        studentRepository.findAllByDeletedAtIsNull().stream()
                .filter(s -> s.getName().toLowerCase().contains(q) || s.getAdmissionNumber().toLowerCase().contains(q))
                .limit(5)
                .forEach(s -> results.add(GlobalSearchResultDto.builder()
                        .category("STUDENT")
                        .id(s.getId())
                        .title(s.getName())
                        .subtitle("Adm No: " + s.getAdmissionNumber() + " | " + s.getClassEntity().getName())
                        .url("/students")
                        .build()));

        // Search Teachers
        teacherRepository.findAllByDeletedAtIsNull().stream()
                .filter(t -> t.getName().toLowerCase().contains(q) || t.getDepartment().toLowerCase().contains(q))
                .limit(5)
                .forEach(t -> results.add(GlobalSearchResultDto.builder()
                        .category("TEACHER")
                        .id(t.getId())
                        .title(t.getName())
                        .subtitle("Dept: " + t.getDepartment())
                        .url("/teachers")
                        .build()));

        // Search Parents
        parentRepository.findAllByDeletedAtIsNull().stream()
                .filter(p -> p.getName().toLowerCase().contains(q) || p.getEmail().toLowerCase().contains(q))
                .limit(5)
                .forEach(p -> results.add(GlobalSearchResultDto.builder()
                        .category("PARENT")
                        .id(p.getId())
                        .title(p.getName())
                        .subtitle("Email: " + p.getEmail())
                        .url("/parents")
                        .build()));

        // Search Classes
        classRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(q))
                .limit(5)
                .forEach(c -> results.add(GlobalSearchResultDto.builder()
                        .category("CLASS")
                        .id(c.getId())
                        .title(c.getName())
                        .subtitle("Year: " + c.getAcademicYear())
                        .url("/academics")
                        .build()));

        // Search Subjects
        subjectRepository.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(q) || s.getCode().toLowerCase().contains(q))
                .limit(5)
                .forEach(s -> results.add(GlobalSearchResultDto.builder()
                        .category("SUBJECT")
                        .id(s.getId())
                        .title(s.getName())
                        .subtitle("Code: " + s.getCode())
                        .url("/academics")
                        .build()));

        return results;
    }
}
