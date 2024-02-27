package com.raphael.msuser.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdateDto {

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 3, max = 100, message = "Tamanho deve ser entre 3 e 100 caracteres")
    private String firstName;

    @NotBlank(message = "O campo não pode estar em branco.")
    @Size(min = 3, max = 100, message = "Tamanho deve ser entre 3 e 100 caracteres")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthdate;

    @NotBlank
    private String cep;

    @NotNull
    private Boolean active;
}
