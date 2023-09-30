package com.n3lx.minidrive.web.controller;

import com.n3lx.minidrive.entity.User;
import com.n3lx.minidrive.service.contract.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
public class FileStorageController {

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody MultipartFile file, @AuthenticationPrincipal User user) {
        var uploadResult = fileStorageService.store(file, user.getId());
        if (uploadResult) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping("/listfiles")
    public ResponseEntity<?> listFiles(@AuthenticationPrincipal User user) {
        var fileList = fileStorageService.listAllFiles(user.getId());
        return ResponseEntity.ok(fileList);
    }

    @RequestMapping(value = "/load")
    public ResponseEntity<?> load(@RequestPart String fileName, @AuthenticationPrincipal User user) {
        var resource = fileStorageService.load(fileName, user.getId());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestPart String fileName, @AuthenticationPrincipal User user) {
        var deleteResult = fileStorageService.delete(fileName, user.getId());
        return ResponseEntity.ok(deleteResult);
    }

    @PatchMapping(value = "/rename")
    public ResponseEntity<?> rename(@RequestPart String currentFileName,
                                    @RequestPart String newFileName,
                                    @AuthenticationPrincipal User user) {
        var renameResult = fileStorageService.rename(currentFileName, newFileName, user.getId());
        return ResponseEntity.ok().build();
    }

}
