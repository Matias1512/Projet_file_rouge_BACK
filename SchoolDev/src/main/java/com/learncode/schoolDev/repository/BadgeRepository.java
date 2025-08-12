package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learncode.schoolDev.model.Badge;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    boolean existsByTitle(String title);
}
