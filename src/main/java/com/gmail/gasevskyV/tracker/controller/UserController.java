package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            Map<String, Boolean> roles = new HashMap<>();
            for (Role r : Role.values()) {
                if (user.getRoles().contains(r)) {
                    roles.put(r.name(), true);
                } else {
                    roles.put(r.name(), false);
                }
            }
            model.addAttribute("roles", roles);
            model.addAttribute("user", user);
        }
        return "userEdit";
    }

    @PostMapping
    public String changeUser(@AuthenticationPrincipal User userAuth,
//

                             @RequestParam String username,
                             @RequestParam String surname,
                             @RequestParam String old_password,
                             @RequestParam String new_password,
                             @RequestParam Map<String, String> map,
//                             @RequestParam String isActive,
                             Model model){

        log.info("username" + username);
        log.info("old" +old_password);
        log.info("new" + new_password);
        map.forEach((key, value)-> log.info("key: "+ key +" value: " + value));
//        log.info("isActive " + isActive);
        return "userEdit";
    }
}
