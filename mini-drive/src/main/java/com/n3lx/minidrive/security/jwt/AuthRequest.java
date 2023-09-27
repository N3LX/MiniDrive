package com.n3lx.minidrive.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthRequest {

    private final String username;

    private final String password;

}
