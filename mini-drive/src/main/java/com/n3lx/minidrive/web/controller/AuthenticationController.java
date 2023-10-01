package com.n3lx.minidrive.web.controller;

import com.n3lx.minidrive.dto.UserDTO;
import com.n3lx.minidrive.entity.User;
import com.n3lx.minidrive.web.support.payload.AuthRequest;
import com.n3lx.minidrive.web.support.payload.AuthResponse;
import com.n3lx.minidrive.security.jwt.JWTUtil;
import com.n3lx.minidrive.service.UserService;
import com.n3lx.minidrive.web.support.exception.RestErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JWTUtil jwtUtil;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody AuthRequest request) throws Exception {
        var newUser = UserDTO.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userService.create(newUser);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse(user.getUsername(), token);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/whoami", method = RequestMethod.GET)
    public ResponseEntity<?> whoami(@AuthenticationPrincipal User user) {
        if (user != null) {
            return ResponseEntity.ok(user.getUsername());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message("Unauthenticated user")
                .build());
    }

}
