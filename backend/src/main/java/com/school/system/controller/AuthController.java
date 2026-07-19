package com.school.system.controller;

import com.school.system.dto.*;
import com.school.system.entity.User;
import com.school.system.security.CustomUserDetails;
import com.school.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login, logout, password policies and JWT rotation")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT tokens")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        LoginResponse response = authService.login(request, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue new access token")
    public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        TokenRefreshResponse response = authService.rotateRefreshToken(request, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate user tokens and log out")
    public ResponseEntity<String> logout(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.logout(request.getRefreshToken(), ipAddress);
        return ResponseEntity.ok("Successfully logged out");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset OTP code")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.forgotPassword(request, ipAddress);
        return ResponseEntity.ok("OTP has been sent to your email");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP code validity")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.verifyOtp(request, ipAddress);
        return ResponseEntity.ok("OTP verified successfully");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using verified OTP code")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.resetPassword(request, ipAddress);
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password from active profile dashboard")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.changePassword(userDetails.getId(), request, ipAddress);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<String> roles = user.getRoles().stream().map(r -> r.getName()).toList();
        
        UserProfileDto profile = UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .build();
        return ResponseEntity.ok(profile);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
