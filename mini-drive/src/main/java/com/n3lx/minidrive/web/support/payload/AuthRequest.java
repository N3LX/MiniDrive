package com.n3lx.minidrive.web.support.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthRequest {

    private final String username;

    private final String password;

}
