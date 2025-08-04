package com.mahedee.backend.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mahedee.backend.models.User;
import com.mahedee.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

// The UserDetailsService interface is used to retrieve user-related data.
// It has one method named loadUserByUsername() which finds a user entity based on the username and can be overridden to customize the process of finding the user.
// @Service is a Spring annotation that indicates the class is a service. Spring's component scanning mechanism automatically detects such annotated classes and registers them as beans in the Spring application context.
// The @Service annotation is used to mark a class as a service in the business logic layer of the application. In other words, it is part of the component that contains the main business logic and operates on data.

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // @Autowired is an annotation used for automatic dependency injection
    @Autowired
    UserRepository userRepository;

    // When you annotate a method or class with @Transactional, Spring will
    // automatically manage the transactional behavior of that method or the methods
    // within the annotated class.
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not found with username: " + username));
        return UserDetailsImpl.build(user);
    }
    
}
