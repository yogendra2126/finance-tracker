package com.yogendra.finance_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogendra.finance_tracker.dto.UserRequest;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_EMAIL = "user@example.com";

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldReturnOk() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Test User");
        userRequest.setEmail(TEST_EMAIL);
        userRequest.setPassword("password");

        String userJson = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        // First, register a user so the list is not empty
        User user = new User();
        user.setName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setPassword("password");
        userRepository.save(user);

        mockMvc.perform(get("/api/users")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_shouldReturnOk() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setPassword("password");
        userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setPassword("password");
        userRepository.save(user);

        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("Updated User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPassword("newpassword");

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail(TEST_EMAIL);
        user.setPassword("password");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", user.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL))))
                .andExpect(status().isNoContent());
    }
}
