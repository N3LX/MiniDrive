package com.n3lx.minidrive.web.controller;

import com.n3lx.minidrive.entity.User;
import com.n3lx.minidrive.service.contract.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/storage")
public class FileStorageController {

    @Autowired
    FileStorageService fileStorageService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<?> upload(@RequestBody MultipartFile file, @AuthenticationPrincipal User user) {
        if (file == null) {
            return ResponseEntity.badRequest().build();
        }

        var uploadResult = fileStorageService.store(file, user.getId());
        if (uploadResult) {
            return ResponseEntity.created(URI.create("/api/storage/load")).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/listfiles", method = RequestMethod.GET)
    public ResponseEntity<?> listFiles(@AuthenticationPrincipal User user) {
        var fileList = fileStorageService.listAllFiles(user.getId());
        return ResponseEntity.ok(fileList);
    }

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public ResponseEntity<?> load(@RequestPart String fileName, @AuthenticationPrincipal User user) {
        var resource = fileStorageService.load(fileName, user.getId());
        return ResponseEntity.ok(resource);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@RequestPart String fileName, @AuthenticationPrincipal User user) {
        fileStorageService.delete(fileName, user.getId());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<?> rename(@RequestPart String currentFileName,
                                    @RequestPart String newFileName,
                                    @AuthenticationPrincipal User user) {
        fileStorageService.rename(currentFileName, newFileName, user.getId());
        return ResponseEntity.created(URI.create("/api/storage/load")).build();
    }

}
