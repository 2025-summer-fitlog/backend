package com.comwith.fitlog.config;

import com.comwith.fitlog.users.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public WebSecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    // BCryptPasswordEncoder 빈은 여기에 정의합니다.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ⭐⭐ securityFilterChain 메서드의 파라미터로 AuthenticationSuccessHandler를 주입받습니다. ⭐⭐
    // 이렇게 하면 Spring이 이미 생성해 둔 customAuthenticationSuccessHandler 빈을 이 메서드에 주입해 줍니다.
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler customAuthenticationSuccessHandler // 여기에서 주입받습니다.
    ) throws Exception {
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
                                "/custom-logout",
                                "/**.html"
                        ).permitAll()

                        // 초기 정보 설정 경로는 로그인된 사용자만 접근하도록 변경
                        .requestMatchers("/api/init/**").authenticated()

                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )

                // 폼 로그인 관련 설정
                .formLogin(formLogin -> formLogin
                        .loginPage("/login_test")
                        .loginProcessingUrl("/api/users/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        // ⭐ 주입받은 customAuthenticationSuccessHandler 빈을 직접 사용합니다. ⭐
                        .successHandler(customAuthenticationSuccessHandler) // 메서드 호출이 아닌 빈 참조
                        .failureUrl("/login_test?error=true")
                        .permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable())

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login_test")
                        .defaultSuccessUrl("/fitlog-onboarding.html", true)
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService)
                        )
                        .failureUrl("/login_test?error=oauth_failed")
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/fitlog-onboarding.html?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler("/");
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

    // 일반 폼 로그인 성공 시 JSON 응답을 반환하는 핸들러
    // ⭐ UserDetailsService를 파라미터로 받도록 합니다. Spring이 UserDetailsService 구현체(즉, UserService 빈)를 주입해 줄 것입니다. ⭐
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler(UserDetailsService userDetailsService) {
        System.out.println("customAuthenticationSuccessHandler 빈이 생성됨");
        return new AuthenticationSuccessHandler() {
            private final ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String username = authentication.getName();
                System.out.println("로그인 성공 핸들러 실행됨: " + username);

                String name = username; // 기본값은 username
                try {
                    org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // TODO: 만약 UserDetails 구현체에 실제 사용자 이름(name) 필드가 있다면 여기서 가져오세요.
                    // 예시: if (userDetails instanceof com.comwith.fitlog.users.entity.User) {
                    //           name = ((com.comwith.fitlog.users.entity.User) userDetails).getName();
                    //       }
                } catch (Exception e) {
                    System.err.println("사용자 이름 조회 실패: " + e.getMessage());
                }

                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "로그인 성공!");
                responseBody.put("username", username);
                responseBody.put("name", name);

                objectMapper.writeValue(response.getWriter(), responseBody);
            }
        };
    }

    // ⭐ UserDetailsService 빈을 명시적으로 등록합니다. ⭐
    // UserService가 UserDetailsService 인터페이스를 구현했으므로, UserService 빈을 반환합니다.
    // Spring은 @Service 어노테이션이 붙은 UserService를 자동으로 찾아 이 메서드에 주입해 줄 것입니다.
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }
}