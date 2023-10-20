package com.n3lx.minidrive.service;

import com.n3lx.minidrive.service.contract.FileStorageService;
import com.n3lx.minidrive.utils.PropertiesUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    PropertiesUtil propertiesUtil;

    @PostConstruct
    private void init() {
        var messageBuilder = new StringBuilder();
        messageBuilder.append("Initializing service").append("\n");

        //Ensure that the root folder exists in filesystem and if not - try to create it
        messageBuilder.append("Checking if root directory exists in location ");
        var rootPath = Paths
                .get(propertiesUtil.getRootDirAbsolutePath())
                .normalize()
                .toAbsolutePath();
        messageBuilder.append("\"").append(rootPath).append("\": ");

        if (Files.exists(rootPath)) {
            if (Files.isDirectory(rootPath)) {
                messageBuilder.append("Directory exists");
                log.info(messageBuilder.toString());
                return;
            } else {
                messageBuilder
                        .append("Resource at this location is not a directory").append("\n")
                        .append("Resolve this issue manually and try starting the application again").append("\n")
                        .append("You can change root directory in application.yaml " +
                                "via app.fileStorage.rootAbsolutePath property");
                log.error(messageBuilder.toString());
                System.exit(1);
            }
        } else {
            messageBuilder
                    .append("Directory does not exist").append("\n")
                    .append("Attempting to create a directory ").append(": ");
            try {
                Files.createDirectory(rootPath);
                messageBuilder.append("Success");
            } catch (IOException e) {
                messageBuilder.append("Failure, see the stack trace for more info")
                        .append("Resolve this issue manually and try starting the application again").append("\n")
                        .append("You can change root directory in application.yaml " +
                                "via app.fileStorage.rootAbsolutePath property");
                log.error(messageBuilder.toString(), e);
                System.exit(1);
            }
        }
        log.info(messageBuilder.toString());
    }

    @Override
    public boolean store(MultipartFile file, Long ownerId) {
        var logMessageBuilder = new StringBuilder();
        logMessageBuilder
                .append("File upload attempt:").append("\n")
                .append("Original file name: ").append(file.getOriginalFilename()).append("\n")
                .append("File size (in bytes): ").append(file.getSize()).append("\n")
                .append("Owner: ").append(ownerId).append("\n");

        var userDirectoryPath = generatePathToUserDirectory(ownerId);
        var filePath = generateFilePath(file.getOriginalFilename(), ownerId);
        logMessageBuilder
                .append("Calculated path: ").append(filePath).append("\n");

        try (InputStream inputStream = file.getInputStream()) {
            if (validateFilePath(filePath, ownerId)) {
                if (!Files.exists(userDirectoryPath)) {
                    Files.createDirectory(userDirectoryPath);
                }
                if (Files.exists(filePath)) {
                    throw new FileAlreadyExistsException(filePath.toString());
                }

                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IllegalArgumentException e) {
            logMessageBuilder
                    .append("Upload failed");
            log.debug(logMessageBuilder.toString(), e);
            throw e;
        } catch (IOException e) {
            logMessageBuilder
                    .append("Upload failed");
            log.debug(logMessageBuilder.toString(), e);
            throw new RuntimeException(e);
        }

        logMessageBuilder
                .append("Upload successful");
        log.debug(logMessageBuilder.toString());
        return true;
    }

    @Override
    public Resource load(String filename, Long ownerId) {
        var filePath = generateFilePath(filename, ownerId);
        try {
            var resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File with given name was not found in storage");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            log.warn("Could not load file from path: " + filePath);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> listAllFiles(Long ownerId) {
        var userDirectoryPath = generatePathToUserDirectory(ownerId);
        try (var stream = Files.list(userDirectoryPath)) {
            return stream
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (NotDirectoryException | NoSuchFileException e) {
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String filename, Long ownerId) {
        var filePath = generateFilePath(filename, ownerId);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            log.warn("Could not delete file from path: " + filePath);
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean rename(String currentFilename, String newFileName, Long ownerId) {
        var currentFilePath = generateFilePath(currentFilename, ownerId);
        var newFilePath = generateFilePath(newFileName, ownerId);
        try {
            Files.move(currentFilePath, newFilePath);
        } catch (IOException e) {
            log.warn("Could not rename file from path " + currentFilePath + " to " + newFilePath);
            throw new RuntimeException(e);
        }
        return true;
    }

    private Path generatePathToUserDirectory(Long ownerId) {
        return Paths
                .get(propertiesUtil.getRootDirAbsolutePath())
                .normalize()
                .resolve(ownerId.toString())
                .toAbsolutePath();
    }

    private Path generateFilePath(String fileName, Long ownerId) {
        var userDirectory = generatePathToUserDirectory(ownerId);
        return userDirectory.resolve(fileName);
    }

    private boolean validateFilePath(Path path, Long ownerId) {
        if (!generatePathToUserDirectory(ownerId).equals(path.getParent())) {
            throw new IllegalArgumentException(
                    "Filename contains special characters that prevent it from being properly stored");
        }
        return true;
    }

}
