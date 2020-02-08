package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
                             @RequestParam Map<String, Object> map,
                             Model model) {
        if (userAuth.getRoles().contains(Role.ROLE_ADMIN)) {
            Long id = Long.valueOf(map.get("user_id").toString());

            Optional<User> optionalUser = userRepo.findById(id);
            User user = null;
            l:
            if (optionalUser.isPresent()) {
                user = optionalUser.get();

                String newUsername = map.getOrDefault("username", user.getUsername()).toString();
                String newSurname = map.getOrDefault("surname", user.getSurname()).toString();
                String newPassword = map.getOrDefault("new_password", "").toString();
                String oldPassword = map.getOrDefault("old_password", "").toString();
                log.info("username:" + newUsername);
                log.info("surname:" + newSurname);
                log.info("newpas:" + newPassword);
                log.info("oldpas:" + oldPassword);
                if (!newPassword.equals("")) {
                    if (oldPassword.equals(user.getPassword())) {
                        user.setPassword(newPassword);
                    } else {
                        model.addAttribute("message", "wrong password");
                        break l;
                    }
                }
                user.setUsername(newUsername);
                user.setSurname(newSurname);
                user.setActive(map.containsKey("isActive"));
                user.setRoles(getRoles(map));

                userRepo.save(user);

                model.addAttribute("message", "user updated");
            }
        }

        return "redirect:/user";
    }

    private Set<Role> getRoles(Map<String, Object> map) {
        List<Role> roles = Arrays.asList(Role.values());
        Set<Role> result = new HashSet<>();
        roles.forEach(role -> {
            if (map.keySet().contains(role.name())) {
                result.add(role);
            }
        });
        return result;
    }
}
