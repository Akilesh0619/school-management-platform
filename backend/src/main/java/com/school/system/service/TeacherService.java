package com.school.system.service;

import com.school.system.dto.TeacherCreateRequest;
import com.school.system.dto.TeacherDto;
import com.school.system.entity.Role;
import com.school.system.entity.Teacher;
import com.school.system.entity.User;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.RoleRepository;
import com.school.system.repository.TeacherRepository;
import com.school.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TeacherDto createTeacher(TeacherCreateRequest request) {
        if (teacherRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BadRequestException("A teacher with email " + request.getEmail() + " already exists.");
        }

        // Auto generate user credentials
        long count = teacherRepository.count();
        String cleanName = request.getName().toLowerCase().replaceAll("\\s+", "_");
        String username = "teacher_" + cleanName + "_" + (count + 1);

        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            username += "_" + System.currentTimeMillis() % 1000;
        }

        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new BadRequestException("Role ROLE_TEACHER not initialized"));

        User user = User.builder()
                .username(username)
                .email(request.getEmail())
                .password(passwordEncoder.encode("password"))
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(teacherRole))
                .build();
        userRepository.save(user);

        Teacher teacher = Teacher.builder()
                .user(user)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .qualification(request.getQualification())
                .experience(request.getExperience())
                .salary(request.getSalary())
                .department(request.getDepartment())
                .status("ACTIVE")
                .build();

        Teacher saved = teacherRepository.save(teacher);
        log.info("Teacher profile created successfully: Email: {}", request.getEmail());
        return mapToDto(saved);
    }

    @Transactional
    public TeacherDto updateTeacher(Long id, TeacherCreateRequest request) {
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        // Check email uniqueness if email has changed
        if (!teacher.getEmail().equals(request.getEmail()) && teacherRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BadRequestException("A teacher with email " + request.getEmail() + " already exists.");
        }

        teacher.setName(request.getName());
        teacher.setPhone(request.getPhone());
        teacher.setQualification(request.getQualification());
        teacher.setExperience(request.getExperience());
        teacher.setSalary(request.getSalary());
        teacher.setDepartment(request.getDepartment());

        // Update user account details if present
        if (teacher.getUser() != null) {
            User user = teacher.getUser();
            user.setEmail(request.getEmail());
            userRepository.save(user);
            teacher.setEmail(request.getEmail());
        }

        Teacher updated = teacherRepository.save(teacher);
        log.info("Teacher profile updated successfully: {}", teacher.getEmail());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        teacher.setDeletedAt(LocalDateTime.now());
        teacher.setStatus("INACTIVE");

        // Soft delete user account as well
        if (teacher.getUser() != null) {
            User user = teacher.getUser();
            user.setDeletedAt(LocalDateTime.now());
            user.setEnabled(false);
            userRepository.save(user);
        }

        teacherRepository.save(teacher);
        log.info("Teacher soft-deleted: {}", teacher.getEmail());
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return mapToDto(teacher);
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherByUserId(Long userId) {
        Teacher teacher = teacherRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for user: " + userId));
        return mapToDto(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public TeacherDto mapToDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .userId(teacher.getUser() != null ? teacher.getUser().getId() : null)
                .name(teacher.getName())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .photoUrl(teacher.getPhotoUrl())
                .qualification(teacher.getQualification())
                .experience(teacher.getExperience())
                .salary(teacher.getSalary())
                .department(teacher.getDepartment())
                .status(teacher.getStatus())
                .build();
    }
}
