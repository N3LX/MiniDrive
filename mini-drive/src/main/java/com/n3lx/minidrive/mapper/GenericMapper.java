package com.n3lx.minidrive.mapper;

public interface GenericMapper<Entity, DTO> {

    Entity mapToEntity(DTO dto);

    DTO mapToDTO(Entity entity);

}
