package com.school.system.repository;

import com.school.system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByIdAndDeletedAtIsNull(Long id);

    Optional<Student> findByAdmissionNumberAndDeletedAtIsNull(String admissionNumber);

    List<Student> findAllByDeletedAtIsNull();

    List<Student> findByClassEntityIdAndDeletedAtIsNull(Long classId);

    List<Student> findByParentIdAndDeletedAtIsNull(Long parentId);

    Optional<Student> findByUserUsernameAndDeletedAtIsNull(String username);

    Optional<Student> findByUserIdAndDeletedAtIsNull(Long userId);
    
    boolean existsByAdmissionNumberAndDeletedAtIsNull(String admissionNumber);
}
