package com.school.system.repository;

import com.school.system.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime from, LocalDateTime to);
    List<Event> findAllByOrderByStartTimeAsc();
}
