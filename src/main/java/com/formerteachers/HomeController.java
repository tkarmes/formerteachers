package com.formerteachers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Former Teachers Jobs");
        model.addAttribute("message", "Welcome to FormerTeachers.com");

        model.addAttribute("jobs", jobRepository.findAll());

        return "index";
    }
}