package com.n3lx.minidrive.service.contract;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    boolean store(MultipartFile file, Long ownerId);

    Resource load(String filename, Long ownerId);

    Resource loadMultiple(List<String> filenames, Long ownerId);

    List<String> listAllFiles(Long ownerId);

    List<String> listFiles(Long ownerId, Integer pageNumber, Integer pageSize);

    boolean delete(String filename, Long ownerId);

    boolean rename(String currentFilename, String newFileName, Long ownerId);

}
