package com.redis.service;

import com.redis.entity.User;
import com.redis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceNormalCache {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "user:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    public User createUser(User user) {
        log.info("Saving new user to database");
        User savedUser = userRepository.save(user);

        // Cache the newly created user
        String cacheKey = CACHE_KEY_PREFIX + savedUser.getId();
        log.info("Setting cache for key: {}", cacheKey);
        redisTemplate.opsForValue().set(cacheKey, savedUser, CACHE_TTL);

        return savedUser;
    }

    public User getUser(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;

        // 1. Check Cache
        log.info("Checking cache for key: {}", cacheKey);
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            log.info("Cache hit for key: {}", cacheKey);
            return cachedUser;
        }

        // 2. Cache miss, fetch from Database
        log.info("Cache miss for key: {}. Fetching from Database", cacheKey);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 3. Store in Cache
        log.info("Setting cache for key: {}", cacheKey);
        redisTemplate.opsForValue().set(cacheKey, user, CACHE_TTL);

        return user;
    }

    public List<User> getAllUsers() {
        // Typically we might not cache lists of all entities unless it's small,
        // to keep it simple we query DB.
        log.info("Fetching all users from database");
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        log.info("Updating user in database");
        User updatedUser = userRepository.save(user);

        // Update cache
        String cacheKey = CACHE_KEY_PREFIX + id;
        log.info("Updating cache for key: {}", cacheKey);
        redisTemplate.opsForValue().set(cacheKey, updatedUser, CACHE_TTL);

        return updatedUser;
    }

    public void deleteUser(Long id) {
        log.info("Deleting user from database");
        userRepository.deleteById(id);

        // Evict cache
        String cacheKey = CACHE_KEY_PREFIX + id;
        log.info("Evicting cache for key: {}", cacheKey);
        redisTemplate.delete(cacheKey);
    }
}
