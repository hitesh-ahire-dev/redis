package com.redis.service.impl;

import com.redis.entity.User;
import com.redis.repository.UserRepository;
import com.redis.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // @CachePut always executes the method and updates the cache with the return
    // value
    @Override
    @CachePut(value = "users", key = "#result.id")
    public User createUser(User user) throws Exception {
        log.info("Saving new user to MySQL database with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    // @Cacheable checks the cache first, and only executes the method if the cache
    // misses
    @Override
    @Cacheable(value = "users", key = "#id")
    public User getUser(Long id) throws Exception {
        log.info("Cache miss for user id: {}. Fetching from Database", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() throws Exception {
        log.info("Fetching all users from MySQL database");
        return userRepository.findAll();
    }

    // @CachePut updates the cache explicitly with the returned updated user
    @Override
    @CachePut(value = "users", key = "#id")
    public User updateUser(Long id, User userDetails) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        log.info("Updating user in MySQL database for ID: {}", id);
        return userRepository.save(user);
    }

    // @CacheEvict removes the key from the cache matching the provided id
    @Override
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) throws Exception {
        log.info("Deleting user from MySQL database for ID: {}", id);
        userRepository.deleteById(id);
    }
}
