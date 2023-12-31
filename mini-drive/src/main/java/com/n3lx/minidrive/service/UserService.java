package com.n3lx.minidrive.service;

import com.n3lx.minidrive.dto.UserDTO;
import com.n3lx.minidrive.mapper.UserMapper;
import com.n3lx.minidrive.repository.UserRepository;
import com.n3lx.minidrive.service.contract.GenericCrudService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    @Override
    public UserDTO create(UserDTO userDTO) {
        var isUsernameAvailable = userRepository.findByUsername(userDTO.getUsername()).isEmpty();
        if (validateDTO(userDTO) && isUsernameAvailable) {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            var user = userMapper.mapToEntity(userDTO);
            user.setRoles(getDefaultRoles());

            var savedObject = userRepository.save(user);
            return userMapper.mapToDTO(savedObject);
        } else {
            throw new BadCredentialsException("User with same username already exists");
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
        if (isPasswordChanged && validateDTO(userDTO)) {
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

    private boolean validateDTO(UserDTO userDTO) {
        var validator = validatorFactory.getValidator();
        var violations = validator.validate(userDTO);
        if (violations.isEmpty()) {
            return true;
        }
        throw new ConstraintViolationException(violations);
    }

    private Set<String> getDefaultRoles() {
        return Set.of("ROLE_USER");
    }

}
