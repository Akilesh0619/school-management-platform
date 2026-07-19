package com.school.system.service;

import com.school.system.dto.*;
import com.school.system.entity.Otp;
import com.school.system.entity.RefreshToken;
import com.school.system.entity.User;
import com.school.system.exception.BadRequestException;
import com.school.system.exception.ResourceNotFoundException;
import com.school.system.exception.UnauthorizedException;
import com.school.system.repository.OtpRepository;
import com.school.system.repository.RefreshTokenRepository;
import com.school.system.repository.UserRepository;
import com.school.system.security.CustomUserDetails;
import com.school.system.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;
    private final AuditLogService auditLogService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenDurationMs;

    @Value("${app.security.account-lock.max-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.security.account-lock.lock-duration-minutes:15}")
    private int lockDurationMinutes;

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmailAndDeletedAtIsNull(request.getUsernameOrEmail()))
                .orElseThrow(() -> {
                    auditLogService.log(request.getUsernameOrEmail(), "LOGIN_FAILED", "User not found", ipAddress);
                    return new UnauthorizedException("Invalid username or password");
                });

        if (!user.isEnabled()) {
            auditLogService.log(user.getUsername(), "LOGIN_FAILED", "Account disabled", ipAddress);
            throw new UnauthorizedException("Account is disabled");
        }

        if (!user.isAccountNonLocked()) {
            if (user.getLockTime().plusMinutes(lockDurationMinutes).isBefore(LocalDateTime.now())) {
                user.setAccountNonLocked(true);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            } else {
                auditLogService.log(user.getUsername(), "LOGIN_FAILED", "Account is locked", ipAddress);
                throw new UnauthorizedException("Account is temporarily locked. Try again later.");
            }
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);
            if (attempts >= maxFailedAttempts) {
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
                auditLogService.log(user.getUsername(), "ACCOUNT_LOCKED", "Locked due to consecutive failures", ipAddress);
            } else {
                userRepository.save(user);
            }
            auditLogService.log(user.getUsername(), "LOGIN_FAILED", "Incorrect password. Attempt: " + attempts, ipAddress);
            throw new UnauthorizedException("Invalid username or password");
        }

        // Login success, clear attempts
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        
        // Generate Rotating Refresh Token
        String refreshTokenString = UUID.randomUUID().toString();
        
        // Save token in DB and cache inside Redis
        refreshTokenRepository.deleteByUser(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        refreshTokenRepository.save(refreshToken);
        
        // Cache refresh token in Redis for O(1) verify
        try {
            redisTemplate.opsForValue().set(
                    "refresh:token:" + refreshTokenString, 
                    user.getUsername(), 
                    refreshTokenDurationMs, 
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.warn("Redis unavailable for caching refresh token: {}. Using DB storage.", e.getMessage());
        }

        List<String> rolesList = user.getRoles().stream().map(r -> r.getName()).toList();
        auditLogService.log(user.getUsername(), "LOGIN_SUCCESS", "Successfully logged in", ipAddress);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(rolesList)
                .build();
    }

    @Transactional
    public TokenRefreshResponse rotateRefreshToken(TokenRefreshRequest request, String ipAddress) {
        String requestToken = request.getRefreshToken();
        
        // Check Redis cache first, fall back to DB
        String username = null;
        try {
            username = redisTemplate.opsForValue().get("refresh:token:" + requestToken);
        } catch (Exception e) {
            log.warn("Redis unavailable during token refresh check: {}", e.getMessage());
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid Refresh Token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            try { redisTemplate.delete("refresh:token:" + requestToken); } catch (Exception ignored) {}
            throw new UnauthorizedException("Refresh Token has expired. Please sign in again.");
        }

        User user = refreshToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        String newRefreshTokenString = UUID.randomUUID().toString();

        // Delete old token
        refreshTokenRepository.delete(refreshToken);
        try { redisTemplate.delete("refresh:token:" + requestToken); } catch (Exception ignored) {}

        // Create rotated new refresh token
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshTokenString)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        try {
            redisTemplate.opsForValue().set(
                    "refresh:token:" + newRefreshTokenString, 
                    user.getUsername(), 
                    refreshTokenDurationMs, 
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.warn("Redis unavailable for caching rotated token: {}", e.getMessage());
        }

        auditLogService.log(user.getUsername(), "TOKEN_REFRESH", "Rotated refresh token", ipAddress);
        return new TokenRefreshResponse(newAccessToken, newRefreshTokenString);
    }

    @Transactional
    public void logout(String refreshTokenString, String ipAddress) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(refreshTokenString);
        if (tokenOpt.isPresent()) {
            User user = tokenOpt.get().getUser();
            refreshTokenRepository.deleteByUser(user);
            try { redisTemplate.delete("refresh:token:" + refreshTokenString); } catch (Exception ignored) {}
            auditLogService.log(user.getUsername(), "LOGOUT", "Logged out successfully", ipAddress);
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request, String ipAddress) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with email: " + request.getEmail()));

        // Generate 6 Digit Code
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        Otp otp = Otp.builder()
                .email(user.getEmail())
                .otpCode(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .purpose("FORGOT_PASSWORD")
                .verified(false)
                .build();
        otpRepository.save(otp);

        // Send Email
        String mailBody = "<h3>Reset Password Verification</h3>" +
                "<p>Greetings. You requested to reset your password. Use the following 6-digit OTP code to complete verification:</p>" +
                "<h2>" + otpCode + "</h2>" +
                "<p>This code will expire in 15 minutes.</p>";
        mailService.sendEmail(user.getEmail(), "Password Reset OTP - School Management System", mailBody);
        
        auditLogService.log(user.getUsername(), "FORGOT_PASSWORD_REQUEST", "OTP generated and dispatched", ipAddress);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequest request, String ipAddress) {
        Otp otp = otpRepository.findByEmailAndOtpCodeAndPurposeAndVerifiedIsFalse(
                request.getEmail(), request.getOtpCode(), request.getPurpose())
                .orElseThrow(() -> new BadRequestException("Invalid or already used OTP code"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        otp.setVerified(true);
        otpRepository.save(otp);
        auditLogService.log(request.getEmail(), "OTP_VERIFIED", "OTP verification successful for " + request.getPurpose(), ipAddress);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request, String ipAddress) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        Otp otp = otpRepository.findTopByEmailAndPurposeOrderByExpiryTimeDesc(request.getEmail(), "FORGOT_PASSWORD")
                .orElseThrow(() -> new BadRequestException("No OTP requested for password reset"));

        if (!otp.isVerified()) {
            throw new BadRequestException("OTP verification is pending");
        }

        validatePasswordStrength(request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedAttempts(0);
        user.setAccountNonLocked(true);
        userRepository.save(user);

        // Clear verified token state
        otpRepository.delete(otp);

        auditLogService.log(user.getUsername(), "PASSWORD_RESET", "Password successfully reset via OTP verification", ipAddress);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request, String ipAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid old password");
        }

        validatePasswordStrength(request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(user.getUsername(), "PASSWORD_CHANGE", "Password changed via Profile", ipAddress);
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new BadRequestException("Password must contain at least one uppercase, lowercase, digit, and special character");
        }
    }
}
