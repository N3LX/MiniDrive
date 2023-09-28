package com.n3lx.minidrive.service.contract;

import java.util.List;

public interface GenericCrudService<DTO> {

    DTO create(DTO dto);

    DTO getById(Long id);

    List<DTO> getAll();

    DTO update(DTO dto);

    void delete(Long id);

}
