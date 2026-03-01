package com.radis.service;

import com.radis.entity.User;
import java.util.List;

public interface UserService {
    User createUser(User user) throws Exception;

    User getUser(Long id) throws Exception;

    List<User> getAllUsers() throws Exception;

    User updateUser(Long id, User userDetails) throws Exception;

    void deleteUser(Long id) throws Exception;
}
