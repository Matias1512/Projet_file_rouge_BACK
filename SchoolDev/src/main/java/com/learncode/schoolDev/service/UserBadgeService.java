package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.UserBadgeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;

    public UserBadgeService(UserBadgeRepository userBadgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
    }

    public List<UserBadge> getAllUserBadges() {
        return userBadgeRepository.findAll();
    }

    public Optional<UserBadge> getUserBadgeById(Long id) {
        return userBadgeRepository.findById(id);
    }

    public List<UserBadge> getUserBadgesByUser(Long userId) {
        return userBadgeRepository.findByUser_UserId(userId);
    }

    public List<UserBadge> getUserBadgesByBadge(Long badgeId) {
        return userBadgeRepository.findByBadge_BadgeId(badgeId);
    }

    public UserBadge createUserBadge(UserBadge userBadge) {
        return userBadgeRepository.save(userBadge);
    }

    public UserBadge updateUserBadge(Long id, UserBadge updatedUserBadge) {
        return userBadgeRepository.findById(id)
                .map(userBadge -> {
                    userBadge.setUser(updatedUserBadge.getUser());
                    userBadge.setBadge(updatedUserBadge.getBadge());
                    userBadge.setEarnedAt(updatedUserBadge.getEarnedAt());
                    return userBadgeRepository.save(userBadge);
                })
                .orElseThrow(() -> new RuntimeException("UserBadge non trouvé avec ID : " + id));
    }

    public void deleteUserBadge(Long id) {
        userBadgeRepository.deleteById(id);
    }
}

