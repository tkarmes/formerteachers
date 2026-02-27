package com.formerteachers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Arrays;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Former Teachers Jobs");

        List<String> jobs = Arrays.asList(
                "Instructional Designer - Math (Remote, McGraw Hill, $55k–$64k)",
                "Curriculum Specialist K-12 Math (Part-time, Savvas, $37–$40/hr)",
                "Assessment Content Creator - Middle School Math (Khan Academy, $77k–$87k fixed-term)"
        );
        model.addAttribute("jobs", jobs);

        return "index";
    }
}