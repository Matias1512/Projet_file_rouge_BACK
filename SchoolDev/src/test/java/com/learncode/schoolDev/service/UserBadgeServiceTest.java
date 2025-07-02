package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.repository.UserBadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBadgeServiceTest {

    @InjectMocks
    private UserBadgeService userBadgeService;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    private UserBadge userBadge;
    private User user;
    private Badge badge;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(100L);
        user.setUsername("testuser");
        // Ajoute d'autres champs si besoin

        badge = new Badge();
        badge.setBadgeId(10L);
        badge.setName("Gold Badge");
        // Ajoute d'autres champs si besoin

        userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setUnlockedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllUserBadges() {
        List<UserBadge> list = Arrays.asList(userBadge, new UserBadge());
        when(userBadgeRepository.findAll()).thenReturn(list);

        List<UserBadge> result = userBadgeService.getAllUserBadges();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userBadgeRepository).findAll();
    }

    @Test
    void testGetUserBadgeById_Found() {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getBadgeId());
        when(userBadgeRepository.findById(any())).thenReturn(Optional.of(userBadge));

        Optional<UserBadge> result = userBadgeService.getUserBadgeById(key);

        assertTrue(result.isPresent());
        assertEquals(userBadge, result.get());
        verify(userBadgeRepository).findById(key);
    }

    @Test
    void testGetUserBadgeById_NotFound() {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(42L, 11L);
        when(userBadgeRepository.findById(key)).thenReturn(Optional.empty());

        Optional<UserBadge> result = userBadgeService.getUserBadgeById(key);

        assertFalse(result.isPresent());
        verify(userBadgeRepository).findById(key);
    }

    @Test
    void testCreateUserBadge() {
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(userBadge);

        UserBadge input = new UserBadge();
        input.setUser(user);
        input.setBadge(badge);

        UserBadge result = userBadgeService.createUserBadge(input);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(badge, result.getBadge());
        verify(userBadgeRepository).save(input);
    }

    @Test
    void testUpdateUserBadge_Found() {
        UserBadge updated = new UserBadge();
        updated.setUser(user);
        updated.setBadge(badge);
        updated.setUnlockedAt(LocalDateTime.now());

        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getBadgeId());

        when(userBadgeRepository.findById(key)).thenReturn(Optional.of(userBadge));
        when(userBadgeRepository.save(any(UserBadge.class))).thenReturn(updated);

        UserBadge result = userBadgeService.updateUserBadge(key, updated);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(badge, result.getBadge());
        verify(userBadgeRepository).findById(key);
        verify(userBadgeRepository).save(any(UserBadge.class));
    }

    @Test
    void testUpdateUserBadge_NotFound() {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(42L, 99L);
        UserBadge updated = new UserBadge();
        updated.setUser(new User());
        updated.setBadge(new Badge());

        when(userBadgeRepository.findById(key)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () -> userBadgeService.updateUserBadge(key, updated));
        assertTrue(ex.getMessage().contains("UserBadge non trouvé"));
        verify(userBadgeRepository).findById(key);
        verify(userBadgeRepository, never()).save(any());
    }

    @Test
    void testDeleteUserBadge_Success() {
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getBadgeId());
        doNothing().when(userBadgeRepository).deleteById(key);

        userBadgeService.deleteUserBadge(key);

        verify(userBadgeRepository).deleteById(key);
    }
}
