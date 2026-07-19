package com.school.system.repository;

import com.school.system.entity.FeesStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeesStructureRepository extends JpaRepository<FeesStructure, Long> {
    List<FeesStructure> findByClassEntityIdAndAcademicYear(Long classId, String academicYear);
    List<FeesStructure> findByAcademicYear(String academicYear);
}
