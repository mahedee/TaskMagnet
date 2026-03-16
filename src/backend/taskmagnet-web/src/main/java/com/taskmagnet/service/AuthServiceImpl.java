package com.taskmagnet.service;

import com.taskmagnet.dto.AuthResponse;
import com.taskmagnet.dto.LoginRequest;
import com.taskmagnet.dto.SignupRequest;
import com.taskmagnet.entity.Role;
import com.taskmagnet.entity.User;
import com.taskmagnet.enums.RoleName;
import com.taskmagnet.repository.RoleRepository;
import com.taskmagnet.repository.UserRepository;
import com.taskmagnet.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getUsername()
        );

        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDepartment() != null)  user.setDepartment(request.getDepartment());
        if (request.getJobTitle() != null)    user.setJobTitle(request.getJobTitle());

        // Assign default role
        Role defaultRole = roleRepository.findByName(RoleName.ROLE_VIEWER)
                .orElseThrow(() -> new IllegalStateException("Default role not found. Run the seeder first."));
        user.getRoles().add(defaultRole);

        userRepository.save(user);

        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());
        return buildResponse(token, user.getUsername(), user.getEmail(),
                user.getFullName(), Set.of(RoleName.ROLE_VIEWER.name()));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Allow login with email by resolving it to username first
        String usernameToAuth = request.getUsernameOrEmail();
        if (usernameToAuth.contains("@")) {
            usernameToAuth = userRepository.findByEmail(usernameToAuth)
                    .map(User::getUsername)
                    .orElse(usernameToAuth);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameToAuth, request.getPassword())
        );

        String username = authentication.getName();

        // Record successful login
        userRepository.findByUsername(username).ifPresent(u -> {
            u.recordSuccessfulLogin();
            userRepository.save(u);
        });

        String token = jwtTokenProvider.generateToken(authentication);

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        User user = userRepository.findByUsername(username).orElseThrow();
        return buildResponse(token, username, user.getEmail(), user.getFullName(), roles);
    }

    private AuthResponse buildResponse(String token, String username, String email,
                                       String fullName, Set<String> roles) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtTokenProvider.getExpirationMs());
        response.setUsername(username);
        response.setEmail(email);
        response.setFullName(fullName);
        response.setRoles(roles);
        return response;
    }
}
