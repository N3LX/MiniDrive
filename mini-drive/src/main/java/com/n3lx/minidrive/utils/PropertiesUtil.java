package com.n3lx.minidrive.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility for accessing non-standard/custom values from application.properties
 */
@Component
@Getter
public class PropertiesUtil {

    @Value("${app.security.password.minLength}")
    private int passwordMinLength;
    @Value("${app.security.password.maxLength}")
    private int passwordMaxLength;
    @Value("${app.fileStorage.rootDirAbsolutePath}")
    private String rootDirAbsolutePath;

}
