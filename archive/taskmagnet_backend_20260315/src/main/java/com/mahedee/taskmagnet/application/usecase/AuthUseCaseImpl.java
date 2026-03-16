package com.mahedee.taskmagnet.application.usecase;

import com.mahedee.taskmagnet.application.dto.request.LoginRequest;
import com.mahedee.taskmagnet.application.dto.request.SignupRequest;
import com.mahedee.taskmagnet.application.dto.response.JwtResponse;
import com.mahedee.taskmagnet.application.dto.response.MessageResponse;
import com.mahedee.taskmagnet.application.port.in.AuthUseCase;
import com.mahedee.taskmagnet.application.port.out.TokenProvider;
import com.mahedee.taskmagnet.domain.exception.BusinessException;
import com.mahedee.taskmagnet.domain.model.User;
import com.mahedee.taskmagnet.domain.model.auth.ERole;
import com.mahedee.taskmagnet.domain.model.auth.Role;
import com.mahedee.taskmagnet.domain.repository.RoleRepository;
import com.mahedee.taskmagnet.domain.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthUseCaseImpl implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthUseCaseImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("User not found after authentication"));

        return new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    @Transactional
    public MessageResponse register(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already in use");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = resolveRoles(request.getRole());
        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully");
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        Set<Role> roles = new HashSet<>();
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            roles.add(findRole(ERole.ROLE_USER));
        } else {
            for (String roleName : requestedRoles) {
                switch (roleName.toLowerCase()) {
                    case "admin" -> roles.add(findRole(ERole.ROLE_ADMIN));
                    case "mod" -> roles.add(findRole(ERole.ROLE_MODERATOR));
                    default -> roles.add(findRole(ERole.ROLE_USER));
                }
            }
        }
        return roles;
    }

    private Role findRole(ERole eRole) {
        return roleRepository.findByName(eRole)
                .orElseThrow(() -> new BusinessException("Role not found: " + eRole));
    }
}
