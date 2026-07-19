package com.school.system.repository;

import com.school.system.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmailAndOtpCodeAndPurposeAndVerifiedIsFalse(String email, String otpCode, String purpose);

    Optional<Otp> findTopByEmailAndPurposeOrderByExpiryTimeDesc(String email, String purpose);
}
