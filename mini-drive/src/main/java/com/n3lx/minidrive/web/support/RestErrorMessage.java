package com.n3lx.minidrive.web.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Getter
public class RestErrorMessage {

    private Timestamp timestamp;

    private String message;

}
