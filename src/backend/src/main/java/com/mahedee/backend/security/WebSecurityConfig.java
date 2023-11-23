package com.mahedee.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mahedee.backend.security.jwt.AuthEntryPointJwt;
import com.mahedee.backend.security.jwt.AuthTokenFilter;
import com.mahedee.backend.security.services.UserDetailsServiceImpl;

// Configure Spring Security
// The @EnableWebSecurity annotation is used to enable web security in a project.
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;


    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

  /**
   * The function returns a BCryptPasswordEncoder object, which is used for encoding passwords in Java.
   */
    @Bean
    public PasswordEncoder passwordEncoder() {

        // The `BCryptPasswordEncoder()` is a class provided by Spring Security that
        // implements the `PasswordEncoder` interface. It is used for encoding passwords
        // using the BCrypt hashing algorithm.
        return new BCryptPasswordEncoder();
    }

 /**
  * The function returns a DaoAuthenticationProvider with a userDetailsService and passwordEncoder set.
  */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

       // The `DaoAuthenticationProvider` is a class provided by Spring Security that implements the
       // `AuthenticationProvider` interface. It is responsible for authenticating a user based on the
       // provided username and password.

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * This function returns an instance of the AuthenticationManager interface based on the provided
     * AuthenticationConfiguration.
     * 
     * @param authConfig AuthenticationConfiguration is a class that provides the necessary configuration
     * for creating an AuthenticationManager bean. It typically contains information such as the
     * authentication providers, user details service, and other authentication-related settings.
     * @return The method is returning an instance of the AuthenticationManager interface.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

   /**
    * This function configures the security filter chain for the HTTP requests, disabling CSRF
    * protection, setting the authentication entry point for exceptions, configuring session
    * management, and specifying the authorization rules for different request paths.
    * 
    * @param http The `http` parameter is an instance of `HttpSecurity`, which is a class provided by
    * Spring Security. It allows you to configure the security settings for your application.
    * @return The method is returning a SecurityFilterChain object.
    */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
