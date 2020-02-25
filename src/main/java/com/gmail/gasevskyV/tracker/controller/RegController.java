package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.controller.util.ControllerUtils;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.entity.dto.CaptchaResponseDto;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegController {
    private final UserService userService;
    private final RestTemplate restTemplate;

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Value("${google.recaptcha.key.secret}")
    private String captchaSecret;

    @GetMapping("/signup")
    public String regPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String addUser(@RequestParam("password2") String passwordConfirm,
                          @RequestParam("g-recaptcha-response") String captchaResponse,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model) {
        String url = String.format(CAPTCHA_URL, captchaSecret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        boolean isCaptchaError = (response!=null && !response.isSuccess());
        if (isCaptchaError){
            model.addAttribute("captchaError", "Use captcha");
        }

        boolean isConfirmError = StringUtils.isEmpty(passwordConfirm);
        if (isConfirmError){
            model.addAttribute("password2Error", "Password confirmation con't be empty");
        }
        if (user.getPassword()!=null && !user.getPassword().equals(passwordConfirm)){
            model.addAttribute("passwordError", "Passwords are different");
            isConfirmError = true;
        }
        if (isConfirmError || bindingResult.hasErrors() || isCaptchaError) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            errorsMap.forEach((k, v) -> {
                model.addAttribute(k, v);
            });
            model.addAttribute("user", user);
            return "signup";
        } else if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "User already exists!");
            return "signup";
        }


        return "redirect:/loginIN";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        if (userService.activateUser(code)) {
            model.addAttribute("message", "User successfully activated!");
        } else {
            model.addAttribute("message", "Activation code is not found.");
        }
        return "redirect:/loginIN";
    }
}
