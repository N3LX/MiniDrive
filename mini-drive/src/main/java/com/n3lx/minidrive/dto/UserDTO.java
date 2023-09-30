package com.n3lx.minidrive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserDTO {

    private Long id;

    @NotNull
    @Length(max = 64)
    private String username;

    @NotNull
    @Length(max = 255)
    private String password;

    private Set<String> roles;

}
