package com.krieger.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * To handle the security on application level.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // white listed below url's to authenticate.
    private static final String[] WHITE_LIST_URL = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private final BasicAuthConfig basicAuthConfig;

    /**
     * Reading custom configuration properties from property file and creating DOCUMENT, AUTHOR roles with username and password.
     *
     * @return updated roles information with UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // creating DOCUMENT role with username and password
        UserDetails document = User.withUsername(basicAuthConfig.document.getUsername())
                .password(passwordEncoder().encode(basicAuthConfig.document.getPassword()))
                .roles(basicAuthConfig.document.getRole())
                .build();
        // creating AUTHOR role with username and password
        UserDetails author = User.withUsername(basicAuthConfig.author.getUsername())
                .password(passwordEncoder().encode(basicAuthConfig.author.getPassword()))
                .roles(basicAuthConfig.author.getRole())
                .build();
        // saving roles with username and password in InMemoryUserDetailsManager for further authentication process.
        return new InMemoryUserDetailsManager(document, author);
    }

    /**
     * To handle basic security and white list url's throughout the application.
     *
     * @param http to override and provide security.
     * @return updated security filter chain.
     * @throws Exception is something goes wrong with security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // enable basic authentication.
        http.httpBasic(Customizer.withDefaults());
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        // white listing urls to access without authentication.
                                auth.requestMatchers(WHITE_LIST_URL)
                                        .permitAll()
                                        // other than white list urls need to authenticate.
                                        .anyRequest()
                                        .authenticated());
        return http.build();

    }

    /**
     * To encode password.
     *
     * @return password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * To provide authentication.
     *
     * @return authentication provider with updated roles, usernames and passwords.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

}
