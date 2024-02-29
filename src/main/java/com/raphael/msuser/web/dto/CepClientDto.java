package com.raphael.msuser.web.dto;

import lombok.Data;

@Data
public class CepClientDto {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;

}
