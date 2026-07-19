package com.school.system.repository;

import com.school.system.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    Optional<ClassEntity> findByNameAndAcademicYear(String name, String academicYear);

    List<ClassEntity> findAllByAcademicYear(String academicYear);
}
