package com.comwith.fitlog.config;

import com.comwith.fitlog.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;



import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;

    public WebSecurityConfig(CustomOAuth2UserService customOAuth2UserService, UserService userService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userService = userService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ 프록시 헤더 인식 설정 추가
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny())
                )
                .requestCache(cache -> cache
                        .requestCache(new HttpSessionRequestCache())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setContentType("application/json;charset=UTF-8");
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().write("{\"error\":\"로그인이 필요합니다.\",\"code\":\"UNAUTHORIZED\"}");
                            } else {
                                response.sendRedirect("/login_test");
                            }
                        })
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/login_test",
                                "/fitlog-onboarding",
                                "/api/users/send-verification", "/api/users/verify-email",
                                "/api/users/request-reset-password",
                                "/api/users/check-reset-token",
                                "/api/users/reset-password",

                                "/reset-password","/reset-password/**",
                                "/password-reset", "/password-reset/**",
                                "/api/users/register", "/auth/login", "/api/users/login", "/api/users/logout",
                                "/api/users/me",
                                "/api/log/daily/**",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/logout",
                                "/fitlog/**",
                                "/custom-logout",
                                "/.well-known/**",
                                "/error",
                                "/**.html",
                                "/**.css",
                                "/**.js",
                                "/**.png", "/**.jpg", "/**.gif", "/**.ico",
                                "/music/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/api/init/**", "/api/log/**", "/api/profile/**").authenticated()
                        .anyRequest().authenticated()
                )
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login_test")
//                        // .loginProcessingUrl("/api/users/login")
//                        .loginProcessingUrl("/auth/login")
//                        // .usernameParameter("username")
//                        // .passwordParameter("password")
//                        // .successHandler(customAuthenticationSuccessHandler())
//                        // .failureUrl("/login_test?error=true")
//                        .successHandler(jsonLoginSuccessHandler())
//                        .failureHandler(jsonLoginFailureHandler())
//                        .permitAll()
//                )
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login_test")
                        .successHandler(customAuthenticationSuccessHandler())
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService)
                        )
                        .failureUrl("/login_test?error=oauth_failed")
                        // 프론트 리다이렉트 주소 오류 수정 위한 부분
                        .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
                                .baseUri("/login/oauth2/code/*")
                        )
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
            // response.sendRedirect("/fitlog-onboarding");
            response.sendRedirect("https://frontend-liard-nine-85.vercel.app/main");
        };
    }

    // AuthenticationManager 빈 추가
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",  // 로컬 개발환경
                "https://fitlog-2025.duckdns.org",  // 백엔드 서버 도메인 (필요시)
                "https://frontend-liard-nine-85.vercel.app"  // 실제 프론트 배포 주소
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // JSON 로그인 성공 핸들러
    @Bean
    public AuthenticationSuccessHandler jsonLoginSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value()); // 200 OK 상태 코드만 응답
        };
    }

    // JSON 로그인 실패 핸들러
    @Bean
    public AuthenticationFailureHandler jsonLoginFailureHandler() {
        return (request, response, exception) -> {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"); // 401 에러 응답
        };
    }



}
