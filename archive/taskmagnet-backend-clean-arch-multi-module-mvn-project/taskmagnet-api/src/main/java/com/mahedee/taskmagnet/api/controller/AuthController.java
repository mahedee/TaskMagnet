package com.mahedee.taskmagnet.api.controller;

import com.mahedee.taskmagnet.application.dto.request.LoginRequest;
import com.mahedee.taskmagnet.application.dto.request.SignupRequest;
import com.mahedee.taskmagnet.application.dto.response.JwtResponse;
import com.mahedee.taskmagnet.application.dto.response.MessageResponse;
import com.mahedee.taskmagnet.application.port.in.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in to TaskMagnet")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authUseCase.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse response = authUseCase.register(signUpRequest);
        return ResponseEntity.ok(response);
    }
}
