package com.comwith.fitlog.config;

import com.comwith.fitlog.users.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;

    public WebSecurityConfig(CustomOAuth2UserService customOAuth2UserService, UserService userService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userService = userService;
    }

//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return bCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/login_test",
                                "/fitlog-onboarding",
                                "/api/users/send-verification", "/api/users/verify-email",
                                "/api/users/request-reset-password",
                                "/api/users/check-reset-token",
                                "/api/users/reset-password",
                                "/password-reset", "/password-reset/**",
                                "/api/users/register", "/api/users/login", "/api/users/me",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/logout",
                                "/custom-logout",
                                "/.well-known/**",
                                "/error",
                                "/**.html",
                                "/**.css",
                                "/**.js",
                                "/**.png", "/**.jpg", "/**.gif", "/**.ico",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/api/init/**", "/api/log/**", "/api/profile/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login_test")
                        // .loginProcessingUrl("/api/users/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/login_test?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login_test")
                        .successHandler(customAuthenticationSuccessHandler())
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService)
                        )
                        .failureUrl("/login_test?error=oauth_failed")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login_test?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("/fitlog-onboarding");
        };
    }

    // AuthenticationManager 빈 추가
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
