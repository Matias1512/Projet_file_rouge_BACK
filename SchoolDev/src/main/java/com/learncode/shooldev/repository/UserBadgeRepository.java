package com.learncode.schooldev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learncode.schooldev.model.UserBadge;
import com.learncode.schooldev.model.UserBadge.UserBadgeKey;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeKey> {
    List<UserBadge> findByUser_UserId(Long userId);
    List<UserBadge> findByBadge_BadgeId(Long badgeId);
    boolean existsByUser_UserIdAndBadge_BadgeId(Long userId, Long badgeId);

}

