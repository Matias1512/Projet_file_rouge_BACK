package com.learncode.schoolDev.dataInitializer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learncode.schoolDev.model.Badge;
import com.learncode.schoolDev.repository.BadgeRepository;

@Component
public class BadgeDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BadgeDataInitializer.class);
    
    private final BadgeRepository badgeRepository;
    private final ObjectMapper objectMapper;

    public BadgeDataInitializer(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing Badge data...");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ListOfBadge.json");
        if (inputStream == null) {
            throw new FileNotFoundException("ListOfBadge.json not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        List<Badge> badges = Arrays.asList(objectMapper.readValue(inputStream, Badge[].class));

        for (Badge badge : badges) {
            if (!badgeRepository.existsByName(badge.getName())) {
                badgeRepository.save(badge);
            }
        }
        logger.info("Badge data initialized successfully.");
    }
}
