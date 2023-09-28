package com.n3lx.minidrive.service;

import com.n3lx.minidrive.dto.UserDTO;
import com.n3lx.minidrive.mapper.UserMapper;
import com.n3lx.minidrive.repository.UserRepository;
import com.n3lx.minidrive.service.contract.GenericCrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService implements GenericCrudService<UserDTO> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
     * Allows to update a password associated with given user
     *
     * @param userDTO Object representing username password
     * @return An object that should contain encoded password
     */
    @Override
    public UserDTO update(UserDTO userDTO) {
        var existingUser = userRepository.findByUsername(userDTO.getUsername());

        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User " + userDTO.getUsername() + " does not exist");
        }

        var user = userMapper.mapToEntity(userDTO);
        user.setId(existingUser.get().getId());
        user.setRoles(existingUser.get().getRoles());

        var isPasswordChanged = !passwordEncoder.matches(userDTO.getPassword(), existingUser.get().getPassword());
        if (isPasswordChanged && validatePassword(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            var savedObject = userRepository.save(user);
            log.debug("Password for user " + userDTO.getUsername() + " has been changed");
            return userMapper.mapToDTO(savedObject);
        }
        return userDTO;
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
