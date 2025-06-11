package com.yogendra.finance_tracker.controller;

import com.yogendra.finance_tracker.dto.AuthRequest;
import com.yogendra.finance_tracker.dto.AuthResponse;
import com.yogendra.finance_tracker.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(request.getEmail());

            // Return the token in the response
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            // Return 401 Unauthorized if authentication fails
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
