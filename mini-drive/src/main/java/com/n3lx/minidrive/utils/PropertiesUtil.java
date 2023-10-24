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

    public static final int usernameMinLength = 3;

    /**
     * Maximum supported value is 64, exceeding it will cause the application to work incorrectly
     */
    public static final int usernameMaxLength = 16;

    public static final int tempDirArchiveRetentionInSeconds = 600;

    @Getter
    @Value("${app.fileStorage.rootDirAbsolutePath}")
    private String rootDirAbsolutePath;

    @Getter
    @Value("${app.fileStorage.tempDirName}")
    private String tempDirName;

}
