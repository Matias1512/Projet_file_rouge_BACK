package com.learncode.schoolDev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.model.UserBadge.UserBadgeKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UserBadgeKey> {
    List<UserBadge> findByUser_UserId(Long userId);
    List<UserBadge> findByBadge_Id(long badgeId);
    boolean existsByUser_UserIdAndBadge_Id(Long userId, Long badgeId);
    Optional<UserBadge> findByUser_UserIdAndBadge_Id(Long userId, Long badgeId);

}

