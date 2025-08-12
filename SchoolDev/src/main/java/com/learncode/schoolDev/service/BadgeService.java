package com.learncode.schoolDev.service;

import org.springframework.stereotype.Service;

import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.repository.BadgeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Optional<Badge> getBadgeById(Long id) {
        return badgeRepository.findById(id);
    }

    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    public Optional<Badge> updateBadge(Long id, Badge updatedBadge) {
        return badgeRepository.findById(id)
                .map(badge -> {
                    badge.setTitle(updatedBadge.getTitle());
                    badge.setDescription(updatedBadge.getDescription());
                    badge.setIcon(updatedBadge.getIcon());
                    badge.setLevel(updatedBadge.getLevel());
                    badge.setColor(updatedBadge.getColor());
                    badge.setTotal(updatedBadge.getTotal());
                    badge.setUnlockRequirement(updatedBadge.getUnlockRequirement());
                    return badgeRepository.save(badge);
                });
    }

    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
}
