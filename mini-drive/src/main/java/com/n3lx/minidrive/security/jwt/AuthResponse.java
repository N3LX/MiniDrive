package com.n3lx.minidrive.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponse {

    private final String username;

    private final String token;

}
