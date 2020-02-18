package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@AllArgsConstructor
@Controller
@Slf4j
//@RequestMapping("/login")
public class LoginController {
//    private final UserRepo userRepo;
    private final UserService userService;
    private final PasswordEncoder encoder;
    //    @PostMapping("/login")
//    public String login(
//            @RequestParam String username,
//            @RequestParam String password) {
//        User user = userRepo.findByUsername(username);
//        if (user.getPassword().equals(password)) {
//            return "redirect:/main";
//        } else {
//            return "/login";
//        }
//
//
//    }
    @GetMapping("/oauth")
    public String user(@AuthenticationPrincipal OAuth2User oAuth2User) {
//        oAuth2User.getAttributes().forEach((k,v)-> System.out.println("key: " +k+"#
// value:" + v));
        userService.oauth(oAuth2User); //page=1&limit=50


        return "redirect:/success";
    }
}
