package com.n3lx.minidrive.service;

import com.n3lx.minidrive.entity.User;
import com.n3lx.minidrive.mapper.UserMapper;
import com.n3lx.minidrive.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Autowired
    @InjectMocks
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void setup() {
        Mockito.reset(userRepository);
    }

    public User getTestUser() {
        return User.builder()
                .id(1L)
                .username("testUser")
                .password(passwordEncoder.encode("12345678"))
                .roles(Set.of("ROLE_USER"))
                .build();
    }

    @Test
    public void getAll_withEmptyRepository_returnsEmptyList() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of());
        var actualList = userService.getAll();

        assertEquals(List.of(), actualList);
    }

    @Test
    public void getAll_withNonEmptyRepository_returnsListOfUserDTO() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));
        var actualList = userService.getAll();

        assertEquals(List.of(userDTO), actualList);
    }

    @Test
    public void getById_withExistingId_returnsUserDTO() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        var actualUserDTO = userService.getById(1L);

        assertEquals(userDTO, actualUserDTO);
    }

    @Test
    public void getById_withNotExistingId_returnsNull() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        var actualUserDTO = userService.getById(1L);

        assertNull(actualUserDTO);
    }

    @Test
    public void getByUsername_withExistingUser_returnsUserDTO() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);

        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        var actualUserDTO = userService.getByUsername(user.getUsername());

        assertEquals(userDTO, actualUserDTO);
    }

    @Test
    public void getByUsername_withNotExistingUser_returnsNull() {
        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());
        var actualUserDTO = userService.getByUsername("testUser");

        assertNull(actualUserDTO);
    }

    @Test
    public void create_withValidCredentials_returnsUserDTO() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);
        userDTO.setPassword("12345678");

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        var actualUserDTO = userService.create(userDTO);

        assertEquals(user.getUsername(), actualUserDTO.getUsername());
        assertEquals(user.getPassword(), actualUserDTO.getPassword());
    }

    @Test
    public void create_withIncorrectPassword_throwsException() {
        var userDTO = userMapper.mapToDTO(getTestUser());
        userDTO.setPassword("123");

        var exception = assertThrows(BadCredentialsException.class, () -> userService.create(userDTO));

        assertEquals("Password must be between 8 and 32 characters in length", exception.getMessage());
    }

    @Test
    public void create_withSameUsernameAlreadyInDb_throwsException() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);
        userDTO.setPassword("12345678");

        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        var exception = assertThrows(BadCredentialsException.class, () -> userService.create(userDTO));

        assertEquals("User with same username already exists", exception.getMessage());
    }

    @Test
    public void update_withNoUserInDb_throwsException() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);

        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());
        var exception = assertThrows(IllegalArgumentException.class, () -> userService.update(userDTO));

        assertEquals("User " + user.getUsername() + " does not exist", exception.getMessage());
    }

    @Test
    public void update_withUserInDbAndInvalidPassword_throwsException() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);
        userDTO.setPassword("1234567");

        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        var exception = assertThrows(BadCredentialsException.class, () -> userService.update(userDTO));

        assertEquals("Password must be between 8 and 32 characters in length", exception.getMessage());
    }

    @Test
    public void update_withCorrectCredentials_updatesUser() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);
        userDTO.setPassword("123456789");
        var userWithModifiedPassword = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password((passwordEncoder.encode(userDTO.getPassword())))
                .roles(user.getRoles())
                .build();

        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(userWithModifiedPassword);
        var actualUserDto = userService.update(userDTO);

        assertEquals(userDTO.getUsername(), actualUserDto.getUsername());
        assertTrue(passwordEncoder.matches(userDTO.getPassword(), actualUserDto.getPassword()));
    }

    @Test
    public void update_withCorrectSamePasswordAsCurrentOne_doesNothing() {
        var user = getTestUser();
        var userDTO = userMapper.mapToDTO(user);
        userDTO.setPassword("12345678");

        Mockito.when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        var actualUserDto = userService.update(userDTO);

        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
        assertEquals(userDTO.getUsername(), actualUserDto.getUsername());
        assertEquals(userDTO.getPassword(), actualUserDto.getPassword());
    }

}
