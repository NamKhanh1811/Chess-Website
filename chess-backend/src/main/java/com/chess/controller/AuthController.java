package com.chess.controller;

import com.chess.model.User;
import com.chess.service.JwtService;
import com.chess.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        User saved = userService.register(user);
        String token = jwtService.generateToken(saved.getUsername());

        saved.setPassword((String) null); // tránh lỗi nulltype

        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("user", saved);
        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        return userService.login(username, password)
                .map(u -> {
                    u.setPassword((String) null);

                    Map<String, Object> res = new HashMap<>();
                    res.put("token", jwtService.generateToken(u.getUsername()));
                    res.put("user", u);
                    return res;
                })
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }
}
