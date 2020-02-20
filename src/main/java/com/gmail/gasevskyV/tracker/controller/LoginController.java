package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
@Slf4j

public class LoginController {

    private final UserService userService;

    @GetMapping("/oauth")
    public String user(@AuthenticationPrincipal OAuth2User oAuth2User) {
        userService.oauth(oAuth2User);
        return "redirect:/success";
    }
}
