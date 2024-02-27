package com.raphael.msuser.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raphael.msuser.exception.NotificationProcessingException;
import com.raphael.msuser.infra.mqueue.MsNotificationPublisher;
import com.raphael.msuser.jwt.JwtToken;
import com.raphael.msuser.jwt.JwtUserDetailsService;
import com.raphael.msuser.web.dto.UserLoginDto;
import com.raphael.msuser.web.dto.UserResponseDto;
import com.raphael.msuser.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Recurso para proceder com a autenticação na API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1")
public class AuthenticationController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;
    private final MsNotificationPublisher notificationPublisher;

    @Operation(summary = "Autenticar na API", description = "Recurso de autenticação na API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso e retorno de um Bearer token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Campo(s) Inválido(s) ",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginDto loginDto, HttpServletRequest request) {
        log.info("Processo de autenticação pelo login {}", loginDto.getEmail());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

            authenticationManager.authenticate(authenticationToken);

            JwtToken token = detailsService.getTokenAuthenticated(loginDto.getEmail());

            notificationPublisher.sendNotification(loginDto.getEmail(), "LOGIN");
            return ResponseEntity.ok(token);
        } catch (AuthenticationException ex) {
            log.warn("Bad Credentials from username '{}'", loginDto.getEmail());
        } catch (JsonProcessingException e) {
            throw new NotificationProcessingException(e.getMessage());
        }
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciais Inválidas"));
    }
}
