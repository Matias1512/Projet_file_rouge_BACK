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
import static org.mockito.ArgumentMatchers.any;
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
        badge.setId(10L);
        badge.setTitle("Gold Badge");
        badge.setDescription("Test badge description");
        badge.setIcon("gold-icon");
        badge.setLevel(1);
        badge.setColor("gold.500");
        badge.setTotal(10);

        userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setUnlockedAt(LocalDateTime.now());
        userBadge.setCurrent(0);
        userBadge.setUnlocked(true);
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
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getId());
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

        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getId());

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
        UserBadge.UserBadgeKey key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getId());
        doNothing().when(userBadgeRepository).deleteById(key);

        userBadgeService.deleteUserBadge(key);

        verify(userBadgeRepository).deleteById(key);
    }

    @Test
    void testGetUserBadgesByUser() {
        UserBadge ub1 = new UserBadge();
        UserBadge ub2 = new UserBadge();
        when(userBadgeRepository.findByUser_UserId(5L)).thenReturn(List.of(ub1, ub2));

        List<UserBadge> result = userBadgeService.getUserBadgesByUser(5L);

        assertEquals(2, result.size());
        verify(userBadgeRepository).findByUser_UserId(5L);
    }

    @Test
    void testGetUserBadgesByBadge() {
        UserBadge ub = new UserBadge();
        when(userBadgeRepository.findByBadge_Id(8L)).thenReturn(List.of(ub));

        List<UserBadge> result = userBadgeService.getUserBadgesByBadge(8L);

        assertEquals(1, result.size());
        verify(userBadgeRepository).findByBadge_Id(8L);
    }

    @Test
    void testAssignBadgeIfNotExists_assignsBadge() {
        user = new User();
        user.setUserId(11L);
        badge = new Badge();
        badge.setId(22L);
        badge.setTitle("Test Badge");
        badge.setDescription("Test description");
        badge.setIcon("test-icon");
        badge.setLevel(1);
        badge.setColor("blue.500");
        badge.setTotal(5);

        when(userBadgeRepository.existsByUser_UserIdAndBadge_Id(11L, 22L)).thenReturn(false);

        userBadgeService.assignBadgeIfNotExists(user, badge);

        ArgumentCaptor<UserBadge> captor = ArgumentCaptor.forClass(UserBadge.class);
        verify(userBadgeRepository).save(captor.capture());
        UserBadge saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(badge, saved.getBadge());
    }

    @Test
    void testAssignBadgeIfNotExists_doesNothingIfExists() {
        user = new User();
        user.setUserId(15L);
        badge = new Badge();
        badge.setId(88L);
        badge.setTitle("Existing Badge");
        badge.setDescription("Existing description");
        badge.setIcon("existing-icon");
        badge.setLevel(2);
        badge.setColor("green.500");
        badge.setTotal(10);

        when(userBadgeRepository.existsByUser_UserIdAndBadge_Id(15L, 88L)).thenReturn(true);

        userBadgeService.assignBadgeIfNotExists(user, badge);

        verify(userBadgeRepository, never()).save(any(UserBadge.class));
    }


}
