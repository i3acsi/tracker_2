package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserRepo userRepo;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("userList", userRepo.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String edit(@AuthenticationPrincipal User userAuth,
                       @PathVariable User user,
                       Model model) {
        if (userAuth.getRoles().contains(Role.ROLE_ADMIN)) {
            model.addAttribute("user", user);
        }
        return "userEdit";
    }
}
