package com.raphael.msuser.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateDto {

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 3, max = 100, message = "Tamanho deve ser entre 3 e 100 caracteres")
    private String firstName;

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 3, max = 100, message = "Tamanho deve ser entre 3 e 100 caracteres")
    private String lastName;

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 14, max = 14, message = "Insira um formato de CPF válido: (xxx-xxx-xxx.xx)")
    @CPF
    private String cpf;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;

    @NotBlank(message = "O campo não pode estar em branco.")
    @Email(message = "Insira um e-mail válido.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotBlank
    @Size(min = 9, max = 9, message = "Insira um formato de CEP válido: (xxxxx-xxx)")
    private String cep;

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 6, max = 100, message = "Tamanho deve ser entre 6 e 100 caracteres")
    private String password;

    @NotNull
    private Boolean active;
}
