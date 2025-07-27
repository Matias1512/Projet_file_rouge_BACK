package com.learncode.schoolDev.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingConfigTest {

    private RateLimitingConfig rateLimitingConfig;

    @BeforeEach
    void setUp() {
        rateLimitingConfig = new RateLimitingConfig();
    }

    @Test
    void testCreateBucket_AuthKey() {
        String authKey = "auth:192.168.1.1";
        
        Bucket bucket = rateLimitingConfig.createBucket(authKey);
        
        assertNotNull(bucket);
        // Vérifier que le bucket a été créé avec les limites correctes pour auth
        assertEquals(5, bucket.getAvailableTokens());
    }

    @Test
    void testCreateBucket_GeneralKey() {
        String generalKey = "general:192.168.1.1";
        
        Bucket bucket = rateLimitingConfig.createBucket(generalKey);
        
        assertNotNull(bucket);
        // Vérifier que le bucket a été créé avec les limites correctes pour general
        assertEquals(100, bucket.getAvailableTokens());
    }

    @Test
    void testResolveBucket_AuthKey() {
        String authKey = "auth:10.0.0.1";
        
        Bucket bucket1 = rateLimitingConfig.resolveBucket(authKey);
        Bucket bucket2 = rateLimitingConfig.resolveBucket(authKey);
        
        assertNotNull(bucket1);
        assertNotNull(bucket2);
        // Vérifier que le même bucket est retourné pour la même clé (cache)
        assertSame(bucket1, bucket2);
        assertEquals(5, bucket1.getAvailableTokens());
    }

    @Test
    void testResolveBucket_GeneralKey() {
        String generalKey = "general:10.0.0.1";
        
        Bucket bucket1 = rateLimitingConfig.resolveBucket(generalKey);
        Bucket bucket2 = rateLimitingConfig.resolveBucket(generalKey);
        
        assertNotNull(bucket1);
        assertNotNull(bucket2);
        // Vérifier que le même bucket est retourné pour la même clé (cache)
        assertSame(bucket1, bucket2);
        assertEquals(100, bucket1.getAvailableTokens());
    }

    @Test
    void testResolveBucket_DifferentKeys() {
        String authKey = "auth:192.168.1.1";
        String generalKey = "general:192.168.1.1";
        
        Bucket authBucket = rateLimitingConfig.resolveBucket(authKey);
        Bucket generalBucket = rateLimitingConfig.resolveBucket(generalKey);
        
        assertNotNull(authBucket);
        assertNotNull(generalBucket);
        // Vérifier que des buckets différents sont créés pour des clés différentes
        assertNotSame(authBucket, generalBucket);
        assertEquals(5, authBucket.getAvailableTokens());
        assertEquals(100, generalBucket.getAvailableTokens());
    }

    @Test
    void testCreateBucket_CacheUsage() {
        String key = "test:192.168.1.1";
        
        Bucket bucket1 = rateLimitingConfig.createBucket(key);
        Bucket bucket2 = rateLimitingConfig.createBucket(key);
        
        assertNotNull(bucket1);
        assertNotNull(bucket2);
        // Vérifier que le cache fonctionne - même instance retournée
        assertSame(bucket1, bucket2);
    }

    @Test
    void testBucketConsumption_Auth() {
        String authKey = "auth:test-ip";
        
        Bucket bucket = rateLimitingConfig.resolveBucket(authKey);
        
        // Tester la consommation des tokens
        assertTrue(bucket.tryConsume(1));
        assertEquals(4, bucket.getAvailableTokens());
        
        // Consommer tous les tokens restants
        assertTrue(bucket.tryConsume(4));
        assertEquals(0, bucket.getAvailableTokens());
        
        // Le prochain essai devrait échouer
        assertFalse(bucket.tryConsume(1));
    }

    @Test
    void testBucketConsumption_General() {
        String generalKey = "general:test-ip";
        
        Bucket bucket = rateLimitingConfig.resolveBucket(generalKey);
        
        // Tester la consommation des tokens
        assertTrue(bucket.tryConsume(10));
        assertEquals(90, bucket.getAvailableTokens());
        
        // Consommer plus de tokens
        assertTrue(bucket.tryConsume(50));
        assertEquals(40, bucket.getAvailableTokens());
    }

    @Test
    void testNewBucket_UnknownKeyPrefix() {
        String unknownKey = "unknown:192.168.1.1";
        
        Bucket bucket = rateLimitingConfig.resolveBucket(unknownKey);
        
        assertNotNull(bucket);
        // Les clés non-auth devraient utiliser la configuration générale
        assertEquals(100, bucket.getAvailableTokens());
    }
}