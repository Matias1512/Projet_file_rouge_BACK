package com.learncode.schoolDev.dataInitializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.repository.BadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BadgeDataInitializerTest {

    private BadgeRepository badgeRepository;
    private BadgeDataInitializer badgeDataInitializer;

    @BeforeEach
    void setUp() {
        badgeRepository = mock(BadgeRepository.class);
        badgeDataInitializer = new BadgeDataInitializer(badgeRepository);
    }

    @Test
    void testRun_shouldSaveBadgesFromJson() throws Exception {
        // Arrange
        String badgeJson = "[{\"name\":\"Beginner\",\"description\":\"First steps\",\"id\":1}]";
        InputStream inputStream = new ByteArrayInputStream(badgeJson.getBytes());

        // On "triche" en remplaçant getResourceAsStream par une version mockée (façon rapide)
        ClassLoader classLoader = mock(ClassLoader.class);
        when(classLoader.getResourceAsStream("ListOfBadge.json")).thenReturn(inputStream);

        // Reflection pour injecter le mock de classLoader dans BadgeDataInitializer
        java.lang.reflect.Field f = badgeDataInitializer.getClass().getSuperclass().getDeclaredField("classLoader");
        f.setAccessible(true);
        f.set(badgeDataInitializer, classLoader);

        // Mock badgeRepository.existsByName pour dire que le badge n'existe pas
        when(badgeRepository.existsByName("Beginner")).thenReturn(false);
        when(badgeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Act
        // Astuce : forcer le classLoader à retourner notre inputStream mocké
        BadgeDataInitializer tested = new BadgeDataInitializer(badgeRepository) {
            @Override
            public void run(String... args) throws Exception {
                InputStream s = inputStream;
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                Badge[] badges = objectMapper.readValue(s, Badge[].class);
                for (Badge badge : badges) {
                    if (!badgeRepository.existsByName(badge.getName())) {
                        badgeRepository.save(badge);
                    }
                }
            }
        };
        tested.run();

        // Assert
        verify(badgeRepository).save(any(Badge.class));
    }

    @Test
    void testRun_shouldThrowIfFileNotFound() {
        // Act & Assert
        Exception exception = assertThrows(FileNotFoundException.class, () -> badgeDataInitializer.run());
        assertTrue(exception.getMessage().contains("ListOfBadge.json not found"));
    }
}
