package com.n3lx.minidrive.mapper;

import com.n3lx.minidrive.dto.UserDTO;
import com.n3lx.minidrive.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements GenericMapper<User, UserDTO> {

    @Override
    public User mapToEntity(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getUsername(),
                userDTO.getPassword());
    }

    @Override
    public UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

}
