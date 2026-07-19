package com.school.system.repository;

import com.school.system.entity.FinanceLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FinanceLedgerRepository extends JpaRepository<FinanceLedger, Long> {
    List<FinanceLedger> findAllByOrderByTransactionDateDesc();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinanceLedger f WHERE f.type = 'INCOME'")
    BigDecimal sumTotalIncome();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinanceLedger f WHERE f.type = 'EXPENSE'")
    BigDecimal sumTotalExpense();
}
