package com.learncode.schoolDev.controller;

import com.learncode.schoolDev.model.UserBadge;
import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.service.UserBadgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBadgeControllerTest {

    @InjectMocks
    private UserBadgeController userBadgeController;

    @Mock
    private UserBadgeService userBadgeService;

    private UserBadge userBadge;
    private User user;
    private Badge badge;
    private UserBadge.UserBadgeKey key;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(100L);
        user.setUsername("testuser");

        badge = new Badge();
        badge.setBadgeId(10L);
        badge.setName("Gold Badge");

        userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setUnlockedAt(LocalDateTime.now());

        key = new UserBadge.UserBadgeKey(user.getUserId(), badge.getBadgeId());
    }

    @Test
    void testGetAllUserBadges() {
        List<UserBadge> list = Arrays.asList(userBadge, new UserBadge());
        when(userBadgeService.getAllUserBadges()).thenReturn(list);

        List<UserBadge> result = userBadgeController.getAllUserBadges();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userBadgeService).getAllUserBadges();
    }

    @Test
    void testGetUserBadgeById_Found() {
        when(userBadgeService.getUserBadgeById(key)).thenReturn(Optional.of(userBadge));

        ResponseEntity<UserBadge> response = userBadgeController.getUserBadgeById(user.getUserId(), badge.getBadgeId());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(userBadge, response.getBody());
        verify(userBadgeService).getUserBadgeById(key);
    }

    @Test
    void testGetUserBadgeById_NotFound() {
        when(userBadgeService.getUserBadgeById(key)).thenReturn(Optional.empty());

        ResponseEntity<UserBadge> response = userBadgeController.getUserBadgeById(user.getUserId(), badge.getBadgeId());

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userBadgeService).getUserBadgeById(key);
    }

    @Test
    void testCreateUserBadge_Success() {
        when(userBadgeService.createUserBadge(any(UserBadge.class))).thenReturn(userBadge);

        UserBadge response = userBadgeController.createUserBadge(userBadge);

        assertEquals(userBadge, response);
        verify(userBadgeService).createUserBadge(userBadge);
    }


    @Test
    void testCreateUserBadge_Failure() {
        when(userBadgeService.createUserBadge(any(UserBadge.class))).thenThrow(new RuntimeException("Erreur"));

        Exception ex = assertThrows(RuntimeException.class, () -> userBadgeController.createUserBadge(userBadge));
        assertEquals("Erreur", ex.getMessage());
        verify(userBadgeService).createUserBadge(userBadge);
    }

    @Test
    void testUpdateUserBadge_Success() {
        when(userBadgeService.updateUserBadge(eq(key), any(UserBadge.class))).thenReturn(userBadge);

        ResponseEntity<UserBadge> response = userBadgeController.updateUserBadge(user.getUserId(), badge.getBadgeId(), userBadge);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(userBadge, response.getBody());
        verify(userBadgeService).updateUserBadge(key, userBadge);
    }

    @Test
    void testUpdateUserBadge_NotFound() {
        when(userBadgeService.updateUserBadge(key, userBadge))
                .thenThrow(new RuntimeException("UserBadge non trouvé avec clé : " + key));
    
        ResponseEntity<UserBadge> response = userBadgeController.updateUserBadge(
            user.getUserId(), badge.getBadgeId(), userBadge);
    
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userBadgeService).updateUserBadge(key, userBadge);
    }
    

    @Test
    void testDeleteUserBadge() {
        doNothing().when(userBadgeService).deleteUserBadge(key);

        ResponseEntity<Object> response = userBadgeController.deleteUserBadge(user.getUserId(), badge.getBadgeId());

        assertEquals(204, response.getStatusCode().value());
        verify(userBadgeService).deleteUserBadge(key);
    }

    @Test
    void testGetUserBadgesByUser() {
        UserBadge ub1 = new UserBadge();
        UserBadge ub2 = new UserBadge();
        when(userBadgeService.getUserBadgesByUser(123L)).thenReturn(List.of(ub1, ub2));

        List<UserBadge> result = userBadgeController.getUserBadgesByUser(123L);

        assertEquals(2, result.size());
        verify(userBadgeService).getUserBadgesByUser(123L);
    }

    @Test
    void testGetUserBadgesByBadge() {
        UserBadge ub = new UserBadge();
        when(userBadgeService.getUserBadgesByBadge(77L)).thenReturn(List.of(ub));

        List<UserBadge> result = userBadgeController.getUserBadgesByBadge(77L);

        assertEquals(1, result.size());
        verify(userBadgeService).getUserBadgesByBadge(77L);
    }
}
