package com.raphael.msuser;

import com.raphael.msuser.web.client.AddressClient;
import com.raphael.msuser.web.dto.UpdatePasswordDto;
import com.raphael.msuser.web.dto.UserCreateDto;
import com.raphael.msuser.web.dto.UserResponseDto;
import com.raphael.msuser.web.dto.UserUpdateDto;
import com.raphael.msuser.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    WebTestClient testClient;

    @Autowired
    AddressClient addressClient;

    @Autowired
    ModelMapper modelMapper;

    DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Test
    public void createUser_WithValidData_ReturnUserCreatedWithStatus201() {
        UserResponseDto responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "86071-270",
                        "123456", true))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getFirstName()).isEqualTo("Joao");
        Assertions.assertThat(responseBody.getLastName()).isEqualTo("Silva");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("103.795.210-32");
        Assertions.assertThat(responseBody.getBirthdate().format(formattedDate)).isEqualTo("10-01-2005");
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("joao@email.com");
        Assertions.assertThat(responseBody.getCep()).isEqualTo("86071-270");
        Assertions.assertThat(responseBody.getActive()).isEqualTo(true);
    }

    @Test
    public void createUser_WithInvalidData_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Jo", " ",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joaoemail.com", "86071-270",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("Campo(s) inválido(s)");
    }

    @Test
    public void createUser_WithInvalidEmail_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joao@email", "86071-270",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getErrors().containsValue("Insira um e-mail válido.")).isTrue();
    }

    @Test
    public void createUser_WithInvalidPassword_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "86071-270",
                        " ", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("Campo(s) inválido(s)");

        responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "86071-270",
                        "1234", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getErrors().containsValue("Tamanho deve ser entre 6 e 100 caracteres")).isTrue();
    }

    @Test
    public void createUser_WithEmailAlreadyRegistered_ReturnErrorMessageWithStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "jose@email.com", "86071-270",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("E-mail ou CPF já cadastrado no sistema.");
    }

    @Test
    public void createUser_WithInvalidCpf_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "000.000.000-00", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "86071-270",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getErrors().containsValue(
                "número do registro de contribuinte individual brasileiro (CPF) inválido")).isTrue();
    }

    @Test
    public void createUser_WithCpfAlreadyRegistered_ReturnErrorMessageWithStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "077.166.840-60", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "86071-270",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("E-mail ou CPF já cadastrado no sistema.");
    }

    @Test
    public void createUser_WithInvalidCep_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "8671-70",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getErrors().containsValue("Insira um formato de CEP válido: (xxxxx-xxx)")).isTrue();
    }

    @Test
    public void createUser_WithNonexistentCep_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto(
                        "Joao", "Silva",
                        "103.795.210-32", LocalDate.of(2005, 1, 10),
                        "joao@email.com", "00000-000",
                        "123456", true))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("O CEP informado não existe.");
    }

    @Test
    public void findUser_WithExistingId_ReturnUserWithStatus200() {
        UserResponseDto responseBody = testClient
                .get()
                .uri("/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        Assertions.assertThat(responseBody.getFirstName()).isEqualTo("Maria");
        Assertions.assertThat(responseBody.getLastName()).isEqualTo("Oliveira");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("768.582.450-73");
        Assertions.assertThat(responseBody.getBirthdate().format(formattedDate)).isEqualTo("01-02-2000");
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("maria@email.com");
        Assertions.assertThat(responseBody.getCep()).isEqualTo("01001-000");
        Assertions.assertThat(responseBody.getActive()).isEqualTo(true);

        responseBody = testClient
                .get()
                .uri("/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getFirstName()).isEqualTo("Jose");
        Assertions.assertThat(responseBody.getLastName()).isEqualTo("Silva");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("077.166.840-60");
        Assertions.assertThat(responseBody.getBirthdate().format(formattedDate)).isEqualTo("11-05-1990");
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("jose@email.com");
        Assertions.assertThat(responseBody.getCep()).isEqualTo("69317-288");
        Assertions.assertThat(responseBody.getActive()).isEqualTo(false);
    }

    @Test
    public void findUser_WithNonExistentId_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/v1/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void editUser_WithValidData_ReturnUserCreatedWithStatus200() {
        UserResponseDto responseBody = testClient
                .put()
                .uri("/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdateDto(
                        "Maria", "Silva",
                        LocalDate.of(2005, 5, 10),
                        "86071-270", true))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getFirstName()).isEqualTo("Maria");
        Assertions.assertThat(responseBody.getLastName()).isEqualTo("Silva");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("768.582.450-73");
        Assertions.assertThat(responseBody.getBirthdate().format(formattedDate)).isEqualTo("10-05-2005");
        Assertions.assertThat(responseBody.getEmail()).isEqualTo("maria@email.com");
        Assertions.assertThat(responseBody.getCep()).isEqualTo("86071-270");
        Assertions.assertThat(responseBody.getActive()).isEqualTo(true);
    }

    @Test
    public void editUser_WithInvalidData_ReturnErrorMessageWithStatus422() {

        ErrorMessage responseBody = testClient
                .put()
                .uri("/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdateDto(
                        "Maria", "",
                        LocalDate.of(2005, 5, 10),
                        "86071-", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("Campo(s) inválido(s)");
    }

    @Test
    public void editUser_WithInvalidCep_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .put()
                .uri("/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdateDto(
                        "Maria", "Ferreira",
                        LocalDate.of(2005, 5, 10),
                        "86071-", true))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getErrors().containsValue("Insira um formato de CEP válido: (xxxxx-xxx)")).isTrue();
    }

    @Test
    public void editUser_WithNonexistentCep_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .put()
                .uri("/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdateDto(
                        "Maria", "Ferreira",
                        LocalDate.of(2005, 5, 10),
                        "00000-000", true))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("O CEP informado não existe.");
    }

    @Test
    public void editPassword_WithValidData_ReturnStatus204() {
        testClient
                .put()
                .uri("/v1/users/100/password")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdatePasswordDto("12345678"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .put()
                .uri("/v1/users/101/password")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jose@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdatePasswordDto("123Testando"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void editPassword_FromDifferentUser_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
                .put()
                .uri("/v1/users/0/password")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdatePasswordDto("12345678"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editPassword_WithInvalidFields_ReturnErrorMessageWithStatusCode422() {
        ErrorMessage responseBody = testClient
                .put()
                .uri("/v1/users/100/password")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdatePasswordDto(" "))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("Campo(s) inválido(s)");
    }

    @Test
    public void editPassword_WithInvalidData_ReturnErrorMessageWithStatusCode400() {
        ErrorMessage responseBody = testClient
                .put()
                .uri("/v1/users/100/password")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdatePasswordDto("123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("A nova senha tem que ser diferente da senha atual.");
    }
}
