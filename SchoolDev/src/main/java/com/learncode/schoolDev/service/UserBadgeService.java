package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
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

    public Optional<UserBadge> getUserBadgeById(UserBadge.UserBadgeKey id) {
        return userBadgeRepository.findById(id);
    }

    public UserBadge createUserBadge(UserBadge userBadge) {
        return userBadgeRepository.save(userBadge);
    }

    public UserBadge updateUserBadge(UserBadge.UserBadgeKey id, UserBadge updatedUserBadge) {
        return userBadgeRepository.findById(id)
            .map(userBadge -> {
                userBadge.setUser(updatedUserBadge.getUser());
                userBadge.setBadge(updatedUserBadge.getBadge());
                userBadge.setUnlockedAt(updatedUserBadge.getUnlockedAt());
                return userBadgeRepository.save(userBadge);
            })
            .orElseThrow(() -> new RuntimeException("UserBadge non trouvé avec ID : " + id));
    }

    public void deleteUserBadge(UserBadge.UserBadgeKey id) {
        userBadgeRepository.deleteById(id);
    }

    public void assignBadgeIfNotExists(User user, Badge badge) {
        if (!userBadgeRepository.existsByUser_UserIdAndBadge_Id(user.getUserId(), badge.getId())) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadgeRepository.save(userBadge);
        }
    }

    public List<UserBadge> getUserBadgesByUser(Long userId) {
        return userBadgeRepository.findByUser_UserId(userId);
    }

    public List<UserBadge> getUserBadgesByBadge(long badgeId) {
        return userBadgeRepository.findByBadge_Id(badgeId);
    }
}

