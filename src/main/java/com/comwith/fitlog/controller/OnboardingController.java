package com.comwith.fitlog.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OnboardingController {

    @GetMapping("/fitlog-onboarding")
    public String showOnboardingPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isLoggedIn = auth != null && auth.isAuthenticated() &&
                !auth.getName().equals("anonymousUser");

        String userName = isLoggedIn ? auth.getName() : null;


        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("userName", userName);

        return "fitlog-onboarding";
    }
}
