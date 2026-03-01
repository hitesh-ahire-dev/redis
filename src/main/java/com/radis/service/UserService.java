package com.radis.service;

import com.radis.entity.User;
import com.radis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // @CachePut always executes the method and updates the cache with the return
    // value
    @CachePut(value = "users", key = "#result.id")
    public User createUser(User user) {
        log.info("Saving new user to database");
        return userRepository.save(user);
    }

    // @Cacheable checks the cache first, and only executes the method if the cache
    // misses
    @Cacheable(value = "users", key = "#id")
    public User getUser(Long id) {
        log.info("Cache miss for user id: {}. Fetching from Database", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users from database");
        return userRepository.findAll();
    }

    // @CachePut updates the cache explicitly with the returned updated user
        @CachePut(value = "users", key = "#id")
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        log.info("Updating user in database");
        return userRepository.save(user);
    }

    // @CacheEvict removes the key from the cache matching the provided id
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("Deleting user from database");
        userRepository.deleteById(id);
    }
}
