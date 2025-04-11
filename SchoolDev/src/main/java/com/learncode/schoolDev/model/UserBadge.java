package com.learncode.schoolDev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "User_Badges")
@IdClass(UserBadge.UserBadgeKey.class)
public class UserBadge {

    @Id
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "badgeId", nullable = false)
    @NotNull(message = "L'ID du badge est obligatoire")
    private Badge badge;

    @Column(name = "unlockedAt", nullable = false)
    private LocalDateTime unlockedAt;

    @PrePersist
    protected void onUnlock() {
        unlockedAt = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    // Classe de clé composite
    public static class UserBadgeKey implements Serializable {
        private Long user;
        private Long badge;

        public UserBadgeKey() {}

        public UserBadgeKey(Long user, Long badge) {
            this.user = user;
            this.badge = badge;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserBadgeKey)) return false;
            UserBadgeKey that = (UserBadgeKey) o;
            return Objects.equals(user, that.user) && Objects.equals(badge, that.badge);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, badge);
        }

        
        public void uptdateBadgeOfAUser(Long userId) {
            // Logic to update the badge of a user
            // This could involve checking if the user has completed certain tasks or exercises
            // and then assigning the badge accordingly.
        }
    }
}
