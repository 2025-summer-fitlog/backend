package com.comwith.fitlog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; // 추가 임포트 (로그인 성공 핸들러)
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler; // 추가 임포트

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    // CustomOAuth2UserService를 주입받습니다.
    public WebSecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        // 회원가입 및 로그인 경로는 인증 없이 접근 허용
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/login_test",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/logout",
                                "/custom-logout"
                        ).permitAll()

                        // 소셜 로그인 시작 경로도 허용 (예: /oauth2/authorization/google)
                        // Spring Security가 자동으로 처리하는 경로입니다.
                        .requestMatchers("/api/init/**").permitAll()

                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                // 폼 로그인 관련
                .formLogin(formLogin -> formLogin
                        .loginPage("/login_test") // 로그인페이지 /login_test 로 세팅.
                        .loginProcessingUrl("/login") // 로그인 처리 URL (POST)
                        .defaultSuccessUrl("/dashboard", true) // 로그인 성공 시 /dashboard로 항상 리다이렉트
                        .permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login_test")
                        .defaultSuccessUrl("/dashboard", true)// OAuth2 로그인 활성화

                        /*.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService) // 우리가 만든 CustomOAuth2UserService 연결
                        )
                         */

                        // OAuth2 로그인 성공 후 리다이렉션될 기본 URL 설정 (프론트엔드 URL로 설정)
                        // 예를 들어, 로그인 성공 후 클라이언트가 처리할 `/oauth2/success` 같은 엔드포인트로 보낼 수 있습니다.
                        // 여기서는 임시로 루트 경로로 설정하거나, 별도 핸들러를 정의합니다.
                        // .successHandler(oauth2AuthenticationSuccessHandler()) // 로그인 성공 시 처리할 핸들러 지정
                        .failureUrl("/login_test?error=oauth_failed") // 로그인 실패 시 리다이렉션될 URL (필요시 정의)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login_test?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );


        return http.build();
    }

    // OAuth2 로그인 성공 시 특정 URL로 리다이렉션하는 핸들러
    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        // SimpleUrlAuthenticationSuccessHandler를 사용하여 성공 후 특정 URL로 리다이렉션
        // 여기서는 예시로 루트 경로 "/"로 리다이렉션합니다. 실제로는 프론트엔드 로그인 성공 페이지 등으로 지정해야 합니다.
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler("/");
        handler.setAlwaysUseDefaultTargetUrl(true); // 항상 지정된 URL로 리다이렉션
        return handler;
    }


}