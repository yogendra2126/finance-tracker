package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.dto.UserRequest;
import com.yogendra.finance_tracker.dto.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void registerUser(UserRequest userRequest);
    List<UserResponse> getAllUsers();
    Optional<UserResponse> getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    Optional<UserResponse> findByEmail(String email);
}
