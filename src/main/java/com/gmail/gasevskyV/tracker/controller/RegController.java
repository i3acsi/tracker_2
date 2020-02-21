package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.controller.util.ControllerUtils;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
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

import javax.validation.Valid;
import java.util.Map;

@Controller
@AllArgsConstructor
public class RegController {
    private final UserService userService;

    @Autowired
    private ICaptchaService captchaService;

    @GetMapping("/signup")
    public String regPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String addUser(@RequestParam("password2") String passwordConfirm,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model) {
        boolean isConfirmError = StringUtils.isEmpty(passwordConfirm);
        if (isConfirmError){
            model.addAttribute("password2Error", "Password confirmation con't be empty");
        }
        if (user.getPassword()!=null && !user.getPassword().equals(passwordConfirm)){
            model.addAttribute("passwordError", "Passwords are different");
            isConfirmError = true;
        }
        if (isConfirmError || bindingResult.hasErrors()) {
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
        return "loginIN";
    }
}
