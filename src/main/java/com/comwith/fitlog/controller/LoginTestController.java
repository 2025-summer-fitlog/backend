package com.comwith.fitlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginTestController {

    @GetMapping("/login_test")
    public String showLocalLoginPage() {
        return "login_test";
    }
}
