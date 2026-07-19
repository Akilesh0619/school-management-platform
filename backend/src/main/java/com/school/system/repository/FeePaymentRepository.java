package com.school.system.repository;

import com.school.system.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudentId(Long studentId);
    Optional<FeePayment> findByReceiptNo(String receiptNo);

    @Query("SELECT COALESCE(SUM(fp.amountPaid), 0) FROM FeePayment fp WHERE fp.status = 'PAID'")
    BigDecimal sumTotalCollected();

    @Query("SELECT COUNT(DISTINCT fp.student.id) FROM FeePayment fp WHERE fp.status = 'UNPAID' OR fp.status = 'PARTIAL'")
    long countStudentsWithPendingFees();
}
