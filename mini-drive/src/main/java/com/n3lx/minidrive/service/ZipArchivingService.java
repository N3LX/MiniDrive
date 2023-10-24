package com.n3lx.minidrive.service;

import com.n3lx.minidrive.service.contract.ArchivingService;
import com.n3lx.minidrive.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ZipArchivingService implements ArchivingService {

    @Autowired
    PropertiesUtil propertiesUtil;

    @Override
    public Resource archive(List<Resource> resourceList, Long ownerId) {
        var messageBuilder = new StringBuilder();
        messageBuilder
                .append("User ")
                .append(ownerId)
                .append(" has requested compression of multiple files")
                .append("\n");
        messageBuilder
                .append("Attempting to compress ")
                .append(resourceList.size())
                .append(" files")
                .append("\n");
        createTempDirIfNotCreated();

        var zipFilePath = generatePathToTempDirectory().resolve(resourceList.hashCode() + ".zip");
        try (var fileOutputStream = new FileOutputStream(zipFilePath.toString());
             var zipOutputStream = new ZipOutputStream(fileOutputStream)) {

            for (var resource : resourceList) {
                try (var fileInputStream = new FileInputStream(resource.getFile())) {
                    ZipEntry zipEntry = new ZipEntry(resource.getFile().getName());
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int readLength;
                    while ((readLength = fileInputStream.read(buffer)) >= 0) {
                        zipOutputStream.write(buffer, 0, readLength);
                    }
                }

            }
            messageBuilder
                    .append("Compressed ")
                    .append(resourceList.size())
                    .append(" files to ")
                    .append(zipFilePath)
                    .append("\n");
            return new UrlResource(zipFilePath.toUri());
        } catch (IOException e) {
            messageBuilder.append("Compression failed for ").append(zipFilePath);
            throw new RuntimeException(e);
        } finally {
            log.debug(messageBuilder.toString());
        }
    }

    @Override
    @Scheduled(fixedRate = PropertiesUtil.tempDirArchiveRetentionInSeconds * 1000)
    public void cleanupArchives() {
        log.info("Archive directory cleanup started");
        var tempPath = generatePathToTempDirectory();
        AtomicInteger removedFiles = new AtomicInteger();
        try (var filePaths = Files.walk(tempPath)) {
            filePaths
                    .filter(path -> !path.equals(tempPath))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            removedFiles.getAndIncrement();
                        } catch (IOException e) {
                            log.debug("Could not remove a file during cleanup", e);
                        }
                    });
        } catch (IOException e) {
            log.debug("An error has occurred while traversing temp directory", e);
        }
        log.info("Archive cleanup complete, removed file count: " + removedFiles.get());
    }

    private void createTempDirIfNotCreated() {
        var messageBuilder = new StringBuilder();

        messageBuilder.append("Checking if temporary directory exists in location ");
        var rootPath = Paths
                .get(propertiesUtil.getRootDirAbsolutePath())
                .normalize()
                .resolve(propertiesUtil.getTempDirName())
                .toAbsolutePath();
        messageBuilder.append("\"").append(rootPath).append("\": ");

        if (Files.exists(rootPath)) {
            if (Files.isDirectory(rootPath)) {
                messageBuilder.append("Directory exists");
                log.debug(messageBuilder.toString());
                return;
            } else {
                messageBuilder
                        .append("Resource at this location is not a directory").append("\n")
                        .append("Resolve this issue manually and try starting the application again").append("\n")
                        .append("You can change root directory in application.yaml " +
                                "via app.fileStorage.tempDirName property");
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
                                "via app.fileStorage.tempDirName property");
                log.error(messageBuilder.toString(), e);
                System.exit(1);
            }
        }
        log.debug(messageBuilder.toString());
    }

    private Path generatePathToTempDirectory() {
        return Paths
                .get(propertiesUtil.getRootDirAbsolutePath())
                .normalize()
                .resolve(propertiesUtil.getTempDirName())
                .toAbsolutePath();
    }

}
