package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class RegController {
    private final UserService userService;
    private final PasswordEncoder encoder;

    @GetMapping("/signup")
    public String regPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String addUser(User user, Model model) {
        if (!userService.addUser(user)) {
            model.addAttribute("message", "User already exists!");
            return "/signup";
        }

        return "redirect:/loginIN";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        if (userService.activateUser(code)) {
            model.addAttribute("message", "User successfully activated!");
        } else {
            model.addAttribute("message", "Activation code is not found.");
        }

        return "loginIN";
    }
}
