package com.school.system.service;

import com.school.system.dto.ParentCreateRequest;
import com.school.system.dto.ParentDto;
import com.school.system.entity.Parent;
import com.school.system.entity.Role;
import com.school.system.entity.User;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.ParentRepository;
import com.school.system.repository.RoleRepository;
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
public class ParentService {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ParentDto createParent(ParentCreateRequest request) {
        if (parentRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BadRequestException("A parent with email " + request.getEmail() + " already exists.");
        }

        // Auto-generate credentials
        long count = parentRepository.count();
        String cleanName = request.getName().toLowerCase().replaceAll("\\s+", "_");
        String username = "parent_" + cleanName + "_" + (count + 1);

        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            username += "_" + System.currentTimeMillis() % 1000;
        }

        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseThrow(() -> new BadRequestException("Role ROLE_PARENT not initialized"));

        User user = User.builder()
                .username(username)
                .email(request.getEmail())
                .password(passwordEncoder.encode("password"))
                .enabled(true)
                .accountNonLocked(true)
                .roles(Set.of(parentRole))
                .build();
        userRepository.save(user);

        Parent parent = Parent.builder()
                .user(user)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .occupation(request.getOccupation())
                .relation(request.getRelation())
                .address(request.getAddress())
                .build();

        Parent saved = parentRepository.save(parent);
        log.info("Parent profile created successfully: Email: {}", request.getEmail());
        return mapToDto(saved);
    }

    @Transactional
    public ParentDto updateParent(Long id, ParentCreateRequest request) {
        Parent parent = parentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));

        if (!parent.getEmail().equals(request.getEmail()) && parentRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BadRequestException("A parent with email " + request.getEmail() + " already exists.");
        }

        parent.setName(request.getName());
        parent.setPhone(request.getPhone());
        parent.setOccupation(request.getOccupation());
        parent.setRelation(request.getRelation());
        parent.setAddress(request.getAddress());

        // Update user account details
        if (parent.getUser() != null) {
            User user = parent.getUser();
            user.setEmail(request.getEmail());
            userRepository.save(user);
            parent.setEmail(request.getEmail());
        }

        Parent updated = parentRepository.save(parent);
        log.info("Parent profile updated successfully: {}", parent.getEmail());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));

        parent.setDeletedAt(LocalDateTime.now());

        // Soft delete user account as well
        if (parent.getUser() != null) {
            User user = parent.getUser();
            user.setDeletedAt(LocalDateTime.now());
            user.setEnabled(false);
            userRepository.save(user);
        }

        parentRepository.save(parent);
        log.info("Parent soft-deleted: {}", parent.getEmail());
    }

    @Transactional(readOnly = true)
    public ParentDto getParentById(Long id) {
        Parent parent = parentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + id));
        return mapToDto(parent);
    }

    @Transactional(readOnly = true)
    public ParentDto getParentByUserId(Long userId) {
        Parent parent = parentRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found for user: " + userId));
        return mapToDto(parent);
    }

    @Transactional(readOnly = true)
    public List<ParentDto> getAllParents() {
        return parentRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ParentDto mapToDto(Parent parent) {
        return ParentDto.builder()
                .id(parent.getId())
                .userId(parent.getUser() != null ? parent.getUser().getId() : null)
                .name(parent.getName())
                .email(parent.getEmail())
                .phone(parent.getPhone())
                .occupation(parent.getOccupation())
                .relation(parent.getRelation())
                .address(parent.getAddress())
                .build();
    }
}
