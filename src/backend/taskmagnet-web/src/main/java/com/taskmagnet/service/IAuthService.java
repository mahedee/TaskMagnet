package com.taskmagnet.service;

import com.taskmagnet.dto.AuthResponse;
import com.taskmagnet.dto.LoginRequest;
import com.taskmagnet.dto.SignupRequest;

public interface IAuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
