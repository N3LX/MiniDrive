package com.n3lx.minidrive.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility for accessing non-standard/custom properties and static values used in configuration of non-bean objects
 */
@Component
public class PropertiesUtil {

    public static final int passwordMinLength = 8;

    public static final int passwordMaxLength = 32;

    @Getter
    @Value("${app.fileStorage.rootDirAbsolutePath}")
    private String rootDirAbsolutePath;

}
