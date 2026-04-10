package com.peercollab.controller;
import com.peercollab.model.User;
import com.peercollab.repository.UserRepository;
import com.peercollab.security.UserDetailsImpl;
import com.peercollab.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody Map<String, String> loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.get("username"), loginRequest.get("password")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "token", jwt,
            "id", userDetails.getId(),
            "username", userDetails.getUsername(),
            "email", userDetails.getEmail(),
            "roles", roles
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Map<String, String> signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.get("username"))) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.get("email"))) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(
            null,
            signUpRequest.get("username"),
            encoder.encode(signUpRequest.get("password")),
            signUpRequest.getOrDefault("role", "ROLE_STUDENT"),
            signUpRequest.get("fullName"),
            signUpRequest.get("email")
        );

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
}
