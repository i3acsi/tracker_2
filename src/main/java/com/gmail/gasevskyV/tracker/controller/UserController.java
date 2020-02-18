package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserRepo userRepo;
    private final UserService userService;

    @PreAuthorize("(userService.hasRoleAdmin(oAuth2User))")
    @GetMapping
    public String userList(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("i'm here 28");
        model.addAttribute("userList", userRepo.findAll());
        log.info("i'm here 30");

        return "userList";
    }

    @PreAuthorize("@A.hasRole")
    @GetMapping("{user}")
    public String edit(@AuthenticationPrincipal User userAuth,
                       @AuthenticationPrincipal OAuth2User oAuth2User,
                       @PathVariable User user,
                       Model model) {
        User currentUser = userService.getCurrentUser(userAuth, oAuth2User);
        if (currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
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

    @PostMapping("{user}")
    public String delete(@AuthenticationPrincipal User userAuth,
                         @AuthenticationPrincipal OAuth2User oAuth2User,
                         @PathVariable User user,
                         Model model) {
        User currentUser = userService.getCurrentUser(userAuth, oAuth2User);
        if (currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            userRepo.delete(user);
            model.addAttribute("message", "user was deleted");
        }
        return "redirect:/user";
    }

    @PostMapping
    public String changeUser(@AuthenticationPrincipal User userAuth,
                             @AuthenticationPrincipal OAuth2User oAuth2User,
                             @RequestParam Map<String, Object> map,
                             Model model) {
        User currentUser = userService.getCurrentUser(userAuth, oAuth2User);
        if (currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
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

@Component("A")
@Slf4j
final class AuthorizationComponent
        implements IAuthorizationComponent {
    @Autowired
    private UserService userService;

    @Override
    public boolean hasRole( OAuth2User oAuth2User) {
        Role name = Role.ROLE_ADMIN;
        User user = userService.oauth(oAuth2User);
        AtomicBoolean result = new AtomicBoolean(false);
        user.getAuthorities().forEach(role -> {
            log.info("COMPONENT A" + role.toString());
            log.info("COMPONENT A" + name.toString());
            log.info("COMPONENT A" + role.equals(name));
            if (role.equals(name)) {
                result.set(true);
            }
        });
        return result.get();
    }
}

interface IAuthorizationComponent {

    boolean hasRole(OAuth2User user);

}
