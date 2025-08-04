package com.mahedee.backend.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This class is used to handle what happens when an unauthenticated user tries to access a resource that requires authentication. It's a part of Spring Security's mechanism to handle authentication exceptions

//The @Component annotation at the beginning of the class declaration is a Spring Framework annotation. It tells Spring to treat this class as a component and manage it as a bean in the Spring context.
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    // used to log information. This logger is associated with the AuthEntryPointJwt
    // class.
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    // The commence method is overridden from the AuthenticationEntryPoint
    // interface.
    // This method is called whenever an exception is thrown due to an
    // unauthenticated user trying to access a resource that requires
    // authentication.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // the logger logs the error message from the AuthenticationException.
        // The HTTP response's content type is set to JSON and the status is set to 401
        // (Unauthorized).
        logger.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // A Map named body is created to hold the response details. It includes the
        // status code, error type, error message, and the path of the request.
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        // The body Map is serialized into JSON and written to the response's output
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}
