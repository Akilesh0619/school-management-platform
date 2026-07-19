package com.school.system.repository;

import com.school.system.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByIdAndDeletedAtIsNull(Long id);

    Optional<Parent> findByEmailAndDeletedAtIsNull(String email);

    List<Parent> findAllByDeletedAtIsNull();

    Optional<Parent> findByUserIdAndDeletedAtIsNull(Long userId);

    boolean existsByEmailAndDeletedAtIsNull(String email);
}
