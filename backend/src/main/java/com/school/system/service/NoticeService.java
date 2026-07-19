package com.school.system.service;

import com.school.system.dto.NoticeDto;
import com.school.system.entity.Notice;
import com.school.system.entity.User;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.repository.NoticeRepository;
import com.school.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeDto createNotice(NoticeDto dto, Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachmentPath(dto.getAttachmentPath())
                .targetAudience(dto.getTargetAudience().toUpperCase())
                .createdBy(user)
                .build();

        return mapToDto(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public List<NoticeDto> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoticeDto> getNoticesForAudience(String role) {
        List<String> audiences = List.of("ALL", role.toUpperCase());
        return noticeRepository.findByTargetAudienceInOrderByCreatedAtDesc(audiences).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) throw new ResourceNotFoundException("Notice not found: " + id);
        noticeRepository.deleteById(id);
    }

    private NoticeDto mapToDto(Notice n) {
        return NoticeDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .attachmentPath(n.getAttachmentPath())
                .targetAudience(n.getTargetAudience())
                .createdById(n.getCreatedBy() != null ? n.getCreatedBy().getId() : null)
                .createdByName(n.getCreatedBy() != null ? n.getCreatedBy().getUsername() : "Admin")
                .createdAt(n.getCreatedAt())
                .build();
    }
}
