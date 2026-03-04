package com.formerteachers.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "FormerTeachers.com");
        model.addAttribute("message", "Welcome! This site is dedicated to helping former teachers find meaningful roles.");
        model.addAttribute("subtext", "Jobs in edtech, instructional design, curriculum, and more. Coming soon.");
        return "index";
    }
}