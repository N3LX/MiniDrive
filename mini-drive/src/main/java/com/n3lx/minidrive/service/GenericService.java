package com.n3lx.minidrive.service;

import java.util.List;

public interface GenericService<DTO> {

    DTO create(DTO dto);

    DTO getById(Long id);

    List<DTO> getAll();

    DTO update(DTO dto);

    void delete(Long id);

}
