package com.n3lx.minidrive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserDTO {

    @NotNull
    @Length(max = 64)
    private String username;

    @NotNull
    @Length(max = 255)
    private String password;

}
