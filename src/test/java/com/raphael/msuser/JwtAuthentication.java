package com.raphael.msuser;

import com.raphael.msuser.jwt.JwtToken;
import com.raphael.msuser.web.dto.UserLoginDto;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;
import java.util.function.Consumer;

public class JwtAuthentication {

    public static Consumer<HttpHeaders> getHeaderAuthorization(WebTestClient client, String email, String password) {
        String token = Objects.requireNonNull(client
                .post()
                .uri("/v1/login")
                .bodyValue(new UserLoginDto(email, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody()).getToken();
        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
