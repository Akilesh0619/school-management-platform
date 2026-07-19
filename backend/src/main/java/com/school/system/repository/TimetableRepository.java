package com.school.system.repository;

import com.school.system.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByClassEntityId(Long classId);

    List<Timetable> findByTeacherId(Long teacherId);

    List<Timetable> findByRoomNumber(String roomNumber);

    @Query("SELECT COUNT(t) > 0 FROM Timetable t WHERE t.teacher.id = :teacherId AND t.dayOfWeek = :dayOfWeek AND t.startTime < :endTime AND t.endTime > :startTime AND (:id IS NULL OR t.id <> :id)")
    boolean existsTeacherConflict(
            @Param("teacherId") Long teacherId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("id") Long id
    );

    @Query("SELECT COUNT(t) > 0 FROM Timetable t WHERE t.roomNumber = :roomNumber AND t.dayOfWeek = :dayOfWeek AND t.startTime < :endTime AND t.endTime > :startTime AND (:id IS NULL OR t.id <> :id)")
    boolean existsRoomConflict(
            @Param("roomNumber") String roomNumber,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("id") Long id
    );

    @Query("SELECT COUNT(t) > 0 FROM Timetable t WHERE t.classEntity.id = :classId AND t.dayOfWeek = :dayOfWeek AND t.startTime < :endTime AND t.endTime > :startTime AND (:id IS NULL OR t.id <> :id)")
    boolean existsClassConflict(
            @Param("classId") Long classId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("id") Long id
    );
}
