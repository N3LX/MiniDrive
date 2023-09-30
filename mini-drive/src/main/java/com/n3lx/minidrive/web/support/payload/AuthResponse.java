package com.n3lx.minidrive.web.support.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponse {

    private final String username;

    private final String token;

}
