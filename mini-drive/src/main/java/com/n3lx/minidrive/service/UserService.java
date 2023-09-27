package com.n3lx.minidrive.service;

import com.n3lx.minidrive.dto.UserDTO;
import com.n3lx.minidrive.mapper.UserMapper;
import com.n3lx.minidrive.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService implements GenericService<UserDTO> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${app.security.password.minLength}")
    private int passwordMinLength;
    @Value("${app.security.password.maxLength}")
    private int passwordMaxLength;

    @Override
    public UserDTO create(UserDTO userDTO) {
        if (validatePassword(userDTO.getPassword())
                && userRepository.findByUsername(userDTO.getUsername()).isEmpty()) {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            var user = userMapper.mapToEntity(userDTO);
            user.setRoles(getDefaultRoles());

            var savedObject = userRepository.save(user);
            return userMapper.mapToDTO(savedObject);
        } else {
            throw new IllegalArgumentException("User with same username already exists");
        }
    }

    @Override
    public UserDTO getById(Long id) {
        var user = userRepository.findById(id);
        return user
                .map(value -> userMapper.mapToDTO(value))
                .orElse(null);
    }

    public UserDTO getByUsername(String username) {
        var user = userRepository.findByUsername(username);
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

    /**
     * For now this method allows only for a password change
     */
    @Override
    public UserDTO update(UserDTO userDTO) {
        var existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser.isPresent()) {
            var user = userMapper.mapToEntity(userDTO);
            user.setId(existingUser.get().getId());
            user.setRoles(existingUser.get().getRoles());

            var newPassword = passwordEncoder.encode(user.getPassword());
            if (!newPassword.equals(existingUser.get().getPassword()) && validatePassword(user.getPassword())) {
                user.setPassword(newPassword);
                log.debug("Password for user " + user.getUsername() + " has been changed");
            }

            var savedObject = userRepository.save(user);
            return userMapper.mapToDTO(savedObject);
        } else {
            throw new IllegalArgumentException("User " + userDTO.getUsername() + " does not exist");
        }
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private boolean validatePassword(String password) {
        if (password.length() >= passwordMinLength && password.length() <= passwordMaxLength) {
            return true;
        }
        throw new IllegalArgumentException("Password must be between "
                + passwordMinLength
                + " and "
                + passwordMaxLength +
                " characters in length");
    }

    private Set<String> getDefaultRoles() {
        return Set.of("ROLE_USER");
    }

}
