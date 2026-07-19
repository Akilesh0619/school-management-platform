package com.school.system.repository;

import com.school.system.entity.TeacherLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeacherLeaveRepository extends JpaRepository<TeacherLeave, Long> {
    List<TeacherLeave> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    List<TeacherLeave> findByStatusOrderByCreatedAtDesc(String status);
    List<TeacherLeave> findAllByOrderByCreatedAtDesc();
}
