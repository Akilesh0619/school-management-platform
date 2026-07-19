package com.school.system.repository;

import com.school.system.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByTargetAudienceInOrderByCreatedAtDesc(List<String> audiences);
    List<Notice> findAllByOrderByCreatedAtDesc();
}
