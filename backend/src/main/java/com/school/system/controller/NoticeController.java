package com.school.system.controller;

import com.school.system.dto.NoticeDto;
import com.school.system.security.CustomUserDetails;
import com.school.system.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "Notice Board Module", description = "Endpoints for posting and reading school announcements")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'TEACHER')")
    @Operation(summary = "Publish a new notice to board")
    public ResponseEntity<NoticeDto> createNotice(
            @Valid @RequestBody NoticeDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(noticeService.createNotice(dto, userDetails.getId()), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all notices ordered by post date")
    public ResponseEntity<List<NoticeDto>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @GetMapping("/audience/{role}")
    @Operation(summary = "Get notices targeted for a specific role (e.g. STUDENT, TEACHER, PARENT)")
    public ResponseEntity<List<NoticeDto>> getNoticesForAudience(@PathVariable String role) {
        return ResponseEntity.ok(noticeService.getNoticesForAudience(role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete notice post")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
