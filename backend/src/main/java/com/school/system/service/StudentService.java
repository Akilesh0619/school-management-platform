package com.school.system.service;

import com.school.system.dto.StudentCreateRequest;
import com.school.system.dto.StudentDto;
import com.school.system.entity.*;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentDto createStudent(StudentCreateRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        Section section = null;
        if (request.getSectionId() != null) {
            section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));
        }

        Parent parent = null;
        if (request.getParentId() != null) {
            parent = parentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + request.getParentId()));
        }

        // Generate Admission Number & Roll Number
        long count = studentRepository.count();
        String yearStr = request.getAcademicYear().split("-")[0];
        String admissionNumber = "ADM" + yearStr + String.format("%03d", count + 1);
        String rollNumber = String.valueOf(count + 1);

        // Auto-generate User account
        String cleanName = request.getName().toLowerCase().replaceAll("\\s+", "_");
        String username = "student_" + cleanName + "_" + (count + 1);
        String email = request.getEmail() != null && !request.getEmail().trim().isEmpty() 
                ? request.getEmail() 
                : username + "@school.com";

        if (userRepository.existsByUsernameAndDeletedAtIsNull(username) || userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            username += "_" + System.currentTimeMillis() % 1000;
        }

        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new BadRequestException("Role ROLE_STUDENT not initialized"));

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("password")) // Default password
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(studentRole))
                .build();
        userRepository.save(user);

        Student student = Student.builder()
                .user(user)
                .admissionNumber(admissionNumber)
                .rollNumber(rollNumber)
                .name(request.getName())
                .dob(request.getDob())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .religion(request.getReligion())
                .category(request.getCategory())
                .classEntity(classEntity)
                .section(section)
                .parent(parent)
                .phone(request.getPhone())
                .email(email)
                .status("ACTIVE")
                .medicalInfo(request.getMedicalInfo())
                .emergencyContact(request.getEmergencyContact())
                .academicYear(request.getAcademicYear())
                .build();

        Student savedStudent = studentRepository.save(student);
        log.info("Student profile created successfully: Admission: {}", admissionNumber);
        return mapToDto(savedStudent);
    }

    @Transactional
    public StudentDto updateStudent(Long id, StudentCreateRequest request) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        Section section = null;
        if (request.getSectionId() != null) {
            section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));
        }

        Parent parent = null;
        if (request.getParentId() != null) {
            parent = parentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + request.getParentId()));
        }

        student.setName(request.getName());
        student.setDob(request.getDob());
        student.setGender(request.getGender());
        student.setBloodGroup(request.getBloodGroup());
        student.setReligion(request.getReligion());
        student.setCategory(request.getCategory());
        student.setClassEntity(classEntity);
        student.setSection(section);
        student.setParent(parent);
        student.setPhone(request.getPhone());
        student.setMedicalInfo(request.getMedicalInfo());
        student.setEmergencyContact(request.getEmergencyContact());
        student.setAcademicYear(request.getAcademicYear());

        // Update linked user email if present
        if (student.getUser() != null && request.getEmail() != null) {
            User user = student.getUser();
            user.setEmail(request.getEmail());
            userRepository.save(user);
            student.setEmail(request.getEmail());
        }

        Student updatedStudent = studentRepository.save(student);
        log.info("Student profile updated successfully: {}", student.getAdmissionNumber());
        return mapToDto(updatedStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        student.setDeletedAt(LocalDateTime.now());
        student.setStatus("INACTIVE");
        
        // Soft delete user account as well
        if (student.getUser() != null) {
            User user = student.getUser();
            user.setDeletedAt(LocalDateTime.now());
            user.setEnabled(false);
            userRepository.save(user);
        }

        studentRepository.save(student);
        log.info("Student soft-deleted: {}", student.getAdmissionNumber());
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return mapToDto(student);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for user: " + userId));
        return mapToDto(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByClass(Long classId) {
        return studentRepository.findByClassEntityIdAndDeletedAtIsNull(classId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByParent(Long parentId) {
        return studentRepository.findByParentIdAndDeletedAtIsNull(parentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public StudentDto mapToDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .admissionNumber(student.getAdmissionNumber())
                .rollNumber(student.getRollNumber())
                .name(student.getName())
                .dob(student.getDob())
                .gender(student.getGender())
                .bloodGroup(student.getBloodGroup())
                .religion(student.getReligion())
                .nationality(student.getNationality())
                .category(student.getCategory())
                .photoUrl(student.getPhotoUrl())
                .classId(student.getClassEntity().getId())
                .className(student.getClassEntity().getName())
                .sectionId(student.getSection() != null ? student.getSection().getId() : null)
                .sectionName(student.getSection() != null ? student.getSection().getName() : null)
                .parentId(student.getParent() != null ? student.getParent().getId() : null)
                .parentName(student.getParent() != null ? student.getParent().getName() : null)
                .phone(student.getPhone())
                .email(student.getEmail())
                .status(student.getStatus())
                .medicalInfo(student.getMedicalInfo())
                .emergencyContact(student.getEmergencyContact())
                .academicYear(student.getAcademicYear())
                .build();
    }
}
