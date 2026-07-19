package com.school.system.repository;

import com.school.system.entity.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    List<Marks> findByStudentId(Long studentId);

    List<Marks> findByStudentIdAndExamType(Long studentId, String examType);

    Optional<Marks> findByStudentIdAndSubjectIdAndExamType(Long studentId, Long subjectId, String examType);

    @Query("SELECT AVG(m.marksObtained) FROM Marks m WHERE m.subject.id = :subjectId AND m.examType = :examType")
    BigDecimal findClassAverageBySubjectAndExamType(@Param("subjectId") Long subjectId, @Param("examType") String examType);
}
