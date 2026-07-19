package com.school.system.repository;

import com.school.system.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByClassEntityId(Long classId);

    Optional<Section> findByNameAndClassEntityId(String name, Long classId);
}
