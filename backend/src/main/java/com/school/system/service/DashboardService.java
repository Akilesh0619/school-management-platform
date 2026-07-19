package com.school.system.service;

import com.school.system.dto.DashboardDto;
import com.school.system.entity.AuditLog;
import com.school.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final FinanceLedgerRepository financeLedgerRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public DashboardDto getDashboardStats() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalParents = parentRepository.count();
        long totalClasses = classRepository.count();
        long totalSubjects = subjectRepository.count();
        
        BigDecimal feeCollected = feePaymentRepository.sumTotalCollected();
        long pendingFeesCount = feePaymentRepository.countStudentsWithPendingFees();
        
        long totalNotices = noticeRepository.count();
        long totalEvents = eventRepository.count();
        
        BigDecimal income = financeLedgerRepository.sumTotalIncome();
        BigDecimal expense = financeLedgerRepository.sumTotalExpense();
        
        List<AuditLog> recentLogs = auditLogRepository.findAllByOrderByTimestampDesc();
        List<DashboardDto.RecentActivityDto> activities = recentLogs.stream()
                .limit(10)
                .map(log -> DashboardDto.RecentActivityDto.builder()
                        .username(log.getUsername())
                        .action(log.getAction())
                        .details(log.getDetails())
                        .timestamp(log.getTimestamp() != null ? log.getTimestamp().toString() : "")
                        .build())
                .collect(Collectors.toList());

        return DashboardDto.builder()
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalParents(totalParents)
                .totalClasses(totalClasses)
                .totalSubjects(totalSubjects)
                .totalFeeCollected(feeCollected != null ? feeCollected : BigDecimal.ZERO)
                .studentsWithPendingFees(pendingFeesCount)
                .totalNotices(totalNotices)
                .totalEvents(totalEvents)
                .totalIncome(income != null ? income : BigDecimal.ZERO)
                .totalExpense(expense != null ? expense : BigDecimal.ZERO)
                .recentActivities(activities)
                .build();
    }
}
