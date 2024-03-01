package com.raphael.msuser.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String cpf;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthdate;
    private String email;
    private String cep;
    private Boolean active;
}
