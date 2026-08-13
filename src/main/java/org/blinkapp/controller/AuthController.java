package org.blinkapp.controller;

import org.blinkapp.entity.User;
import org.blinkapp.repository.RefreshTokenRepository;
import org.blinkapp.repository.UsersRepository;
import org.blinkapp.security.JwtUtil;
import org.blinkapp.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(userDetails.getUsername());
    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        if(usersRepository.existsByEmail(user.getEmail())) {
            return "Error: Email address already in use";
        }

        User newUser = new User(
                null,
                user.getEmail(),
                passwordEncoder.encode(user.getPassword()),
                user.getRoles()
        );
        usersRepository.save(newUser);
        return "User registered successfully";
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("token");
        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    if(refreshTokenService.isTokenExpired(token)) {
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expired");
                    }
                    String newJwt = jwtUtil.generateToken(token.getUser().getEmail());
                    return ResponseEntity.ok(Map.of("token", newJwt));
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");

        if( requestToken == null || requestToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh Token is required");
        }

        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    refreshTokenRepository.delete(token);
                    return ResponseEntity.ok("Logged out successfully");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token"));
    }
}
