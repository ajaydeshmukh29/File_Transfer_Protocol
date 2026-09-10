package com.fileflow.fileflowbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fileflow.fileflowbackend.dto.AuthResponse;
import com.fileflow.fileflowbackend.entity.User;
import com.fileflow.fileflowbackend.repository.UserRepository;
import com.fileflow.fileflowbackend.security.JwtUtil;

// NOTE: CORS is configured centrally in SecurityConfig.
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil
    )
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user)
    {
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null)
        {
            return ResponseEntity
                .badRequest()
                .body("Email already registered!");
        }

        // Never store the raw password.
        user.setPassword(
            passwordEncoder.encode(user.getPassword())
        );

        // Every newly registered account is a normal USER.
        // ADMIN accounts should be created/assigned separately.
        user.setRole(User.Role.USER);

        userRepository.save(user);

        return ResponseEntity
            .ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user)
    {
        User existingUser =
            userRepository.findByEmail(user.getEmail());

        if (existingUser == null)
        {
            return ResponseEntity
                .status(404)
                .body("User not found!");
        }

        boolean passwordMatches =
            passwordEncoder.matches(
                user.getPassword(),
                existingUser.getPassword()
            );

        if (!passwordMatches)
        {
            return ResponseEntity
                .status(401)
                .body("Incorrect password!");
        }

        String token =
            jwtUtil.generateToken(existingUser.getEmail());

        AuthResponse response =
            new AuthResponse(
                "Login successful!",
                token,
                existingUser.getName(),
                existingUser.getEmail()
            );

        return ResponseEntity.ok(response);
    }
}