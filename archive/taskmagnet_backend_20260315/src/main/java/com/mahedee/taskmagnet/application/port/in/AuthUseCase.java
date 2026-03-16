package com.mahedee.taskmagnet.application.port.in;

import com.mahedee.taskmagnet.application.dto.request.LoginRequest;
import com.mahedee.taskmagnet.application.dto.request.SignupRequest;
import com.mahedee.taskmagnet.application.dto.response.JwtResponse;
import com.mahedee.taskmagnet.application.dto.response.MessageResponse;

/**
 * Input port defining the authentication use cases.
 */
public interface AuthUseCase {
    JwtResponse login(LoginRequest request);
    MessageResponse register(SignupRequest request);
}
