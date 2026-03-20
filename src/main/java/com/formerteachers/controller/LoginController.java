package com.formerteachers.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // This connects the URL "/login" to your "login.html" file
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}