package com.n3lx.minidrive.service;

import com.n3lx.minidrive.dto.UserDTO;
import com.n3lx.minidrive.mapper.UserMapper;
import com.n3lx.minidrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements GenericService<UserDTO> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDTO create(UserDTO userDTO) {
        var user = userMapper.mapToEntity(userDTO);

        var savedObject = userRepository.save(user);

        return userMapper.mapToDTO(savedObject);
    }

    @Override
    public UserDTO getById(Long id) {
        var user = userRepository.findById(id);
        return user
                .map(value -> userMapper.mapToDTO(value))
                .orElse(null);
    }

    @Override
    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users
                .stream()
                .map(u -> userMapper.mapToDTO(u))
                .toList();
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        if (userRepository.findById(userDTO.getId()).isPresent()) {
            var user = userMapper.mapToEntity(userDTO);
            var savedObject = userRepository.save(user);
            return userMapper.mapToDTO(savedObject);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}
