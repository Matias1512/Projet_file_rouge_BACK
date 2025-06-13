package com.learncode.schooldev.service;

import org.springframework.stereotype.Service;

import com.learncode.schooldev.model.Badge;
import com.learncode.schooldev.repository.BadgeRepository;

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

    public Badge updateBadge(Long id, Badge updatedBadge) {
        return badgeRepository.findById(id)
                .map(badge -> {
                    badge.setName(updatedBadge.getName());
                    badge.setDescription(updatedBadge.getDescription());
                    badge.setIconUrl(updatedBadge.getIconUrl());
                    return badgeRepository.save(badge);
                })
                .orElseThrow(() -> new RuntimeException("Badge non trouvé avec ID : " + id));
    }

    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
}
