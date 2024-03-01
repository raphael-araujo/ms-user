package com.raphael.msuser.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {
    @NotBlank(message = "O campo não pode estar em branco.")
    @Email(message = "Insira um e-mail válido.")
    private String email;

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 6, max = 100, message = "Tamanho deve ser entre 3 e 100 caracteres")
    private String password;
}
