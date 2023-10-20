package com.n3lx.minidrive.dto;

import com.n3lx.minidrive.utils.PropertiesUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    @Size(max = 64)
    private String username;

    @NotNull
    @Size(min = PropertiesUtil.passwordMinLength, max = PropertiesUtil.passwordMaxLength)
    private String password;

    private Set<String> roles;

}
