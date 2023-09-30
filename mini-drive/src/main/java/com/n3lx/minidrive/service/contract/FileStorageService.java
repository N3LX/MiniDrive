package com.n3lx.minidrive.service.contract;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    boolean store(MultipartFile file, Long ownerId);

    MultipartFile load(String filename, Long ownerId);

    List<String> listAllFiles(Long ownerId);

    boolean delete(String filename, Long ownerId);

}
