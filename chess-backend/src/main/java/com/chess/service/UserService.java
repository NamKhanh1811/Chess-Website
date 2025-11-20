package com.chess.service;

import com.chess.model.User;
import com.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // -------- REGISTER --------
    public User register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        // Default ELO
        if (user.getElo() == null) {
            user.setElo(1200.0);
        }

        // Default role
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    // -------- LOGIN --------
    public Optional<User> login(String username, String rawPassword) {

        Optional<User> foundUser = userRepository.findByUsername(username);

        if (foundUser.isEmpty()) return Optional.empty();

        User user = foundUser.get();

        // verify password
        if (encoder.matches(rawPassword, user.getPassword())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }
}
