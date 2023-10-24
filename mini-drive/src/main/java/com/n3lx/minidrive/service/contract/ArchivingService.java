package com.n3lx.minidrive.service.contract;

import org.springframework.core.io.Resource;

import java.util.List;

public interface ArchivingService {

    Resource archive(List<Resource> resourceList, Long ownerId);

    void cleanupArchives();

}
