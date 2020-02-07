package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
@AllArgsConstructor
public class RegController {
    private final UserRepo userRepo;

    @GetMapping("/signup")
    public String regPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String addUser(User user, Model model){
        User userFromDB = userRepo.findByUsername(user.getUsername());

        if (userFromDB != null){
            model.addAttribute("message", "User already exists!");
            return "/signup";
        } else {
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.ROLE_USER));
            userRepo.save(user);
        }

        return "redirect:/login";
    }
}
