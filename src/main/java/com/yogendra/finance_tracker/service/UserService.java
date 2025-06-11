package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByEmail(String email);
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
}
