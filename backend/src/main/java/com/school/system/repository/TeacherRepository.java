package com.school.system.repository;

import com.school.system.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

    Optional<Teacher> findByIdAndDeletedAtIsNull(Long id);

    Optional<Teacher> findByEmailAndDeletedAtIsNull(String email);

    List<Teacher> findAllByDeletedAtIsNull();

    Optional<Teacher> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<Teacher> findByUserUsernameAndDeletedAtIsNull(String username);

    boolean existsByEmailAndDeletedAtIsNull(String email);
}
