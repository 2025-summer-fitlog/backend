package com.comwith.fitlog.config;

import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String loginMethod = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        String providerId = null;
        String email = null;
        String name = null; // 'nickname' 대신 'name' 사용

        if ("google".equals(loginMethod)) {
            providerId = (String) oAuth2User.getAttributes().get(userNameAttributeName);
            email = (String) oAuth2User.getAttributes().get("email");
            name = (String) oAuth2User.getAttributes().get("name"); // 구글은 'name'을 바로 사용
        } else if ("kakao".equals(loginMethod)) {
            providerId = String.valueOf(oAuth2User.getAttributes().get(userNameAttributeName));
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    name = (String) profile.get("nickname"); // 카카오는 'nickname'을 'name'으로 사용
                }
            }
        }

        User user = saveOrUpdate(loginMethod, providerId, email, name); // 'nickname' 대신 'name' 전달

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                userNameAttributeName);
    }

    @Transactional
    protected User saveOrUpdate(String loginMethod, String providerId, String email, String name) { // 'nickname' 대신 'name' 파라미터
        Optional<User> optionalUser = userRepository.findByLoginMethodAndProviderId(loginMethod, providerId);

        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.setName(name); // user.setNickname() -> user.setName()
            user.setEmail(email);
        } else {
            user = User.builder()
                    .loginMethod(loginMethod.toUpperCase())
                    .providerId(providerId)
                    .email(email)
                    .name(name) // .nickname(nickname) -> .name(name)
                    .username(null)
                    .password(null)
                    .build();
        }
        return userRepository.save(user);
    }
}