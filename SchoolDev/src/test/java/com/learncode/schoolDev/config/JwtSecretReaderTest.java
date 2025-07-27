package com.learncode.schoolDev.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtSecretReaderTest {

    private JwtSecretReader jwtSecretReader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        jwtSecretReader = new JwtSecretReader();
        ReflectionTestUtils.setField(jwtSecretReader, "jwtSecretKey", "default-secret-key");
    }

    @Test
    void testGetJwtSecretKey_WhenSecretFileNotExists_ReturnsDefaultKey() {
        // Arrange - mock pour que le fichier n'existe pas
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
             MockedStatic<Paths> mockedPaths = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            mockedPaths.when(() -> Paths.get("/run/secrets/jwt_secret_key")).thenReturn(mockPath);
            mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(false);

            // Act
            String result = jwtSecretReader.getJwtSecretKey();

            // Assert
            assertEquals("default-secret-key", result);
        }
    }

    @Test
    void testGetJwtSecretKey_WhenSecretFileExists_ReturnsSecretFromFile() throws IOException {
        // Arrange - créer un fichier temporaire avec un secret
        Path secretFile = tempDir.resolve("jwt_secret_key");
        Files.writeString(secretFile, "  secret-from-file  "); // avec espaces pour tester trim()

        try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            
            mockedPaths.when(() -> Paths.get("/run/secrets/jwt_secret_key")).thenReturn(secretFile);
            mockedFiles.when(() -> Files.exists(secretFile)).thenReturn(true);
            mockedFiles.when(() -> Files.readString(secretFile)).thenReturn("  secret-from-file  ");

            // Act
            String result = jwtSecretReader.getJwtSecretKey();

            // Assert
            assertEquals("secret-from-file", result);
        }
    }

    @Test
    void testGetJwtSecretKey_WhenIOExceptionOccurs_ReturnsDefaultKey() throws IOException {
        // Arrange - mock pour simuler une IOException
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
             MockedStatic<Paths> mockedPaths = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            mockedPaths.when(() -> Paths.get("/run/secrets/jwt_secret_key")).thenReturn(mockPath);
            mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(true);
            mockedFiles.when(() -> Files.readString(mockPath)).thenThrow(new IOException("Read error"));

            // Act
            String result = jwtSecretReader.getJwtSecretKey();

            // Assert
            assertEquals("default-secret-key", result);
        }
    }

    @Test
    void testGetJwtSecretKey_WithDifferentDefaultValue() {
        // Arrange - tester avec une valeur par défaut différente
        ReflectionTestUtils.setField(jwtSecretReader, "jwtSecretKey", "custom-default");

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
             MockedStatic<Paths> mockedPaths = mockStatic(Paths.class)) {
            
            Path mockPath = mock(Path.class);
            mockedPaths.when(() -> Paths.get("/run/secrets/jwt_secret_key")).thenReturn(mockPath);
            mockedFiles.when(() -> Files.exists(mockPath)).thenReturn(false);

            // Act
            String result = jwtSecretReader.getJwtSecretKey();

            // Assert
            assertEquals("custom-default", result);
        }
    }
}