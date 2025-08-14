package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.dto.UserBadgeCreateRequest;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import com.learncode.schoolDev.service.UserService;
import com.learncode.schoolDev.service.BadgeService;

import java.util.List;
import java.util.Optional;

@Service
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final UserService userService;
    private final BadgeService badgeService;

    public UserBadgeService(UserBadgeRepository userBadgeRepository, UserService userService, BadgeService badgeService) {
        this.userBadgeRepository = userBadgeRepository;
        this.userService = userService;
        this.badgeService = badgeService;
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

    public UserBadge createUserBadge(UserBadgeCreateRequest request) {
        User user = userService.getUserById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID : " + request.getUserId()));
        
        Badge badge = badgeService.getBadgeById(request.getBadgeId())
            .orElseThrow(() -> new RuntimeException("Badge non trouvé avec ID : " + request.getBadgeId()));

        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setCurrent(request.getCurrent());
        userBadge.setUnlocked(request.getUnlocked());

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

