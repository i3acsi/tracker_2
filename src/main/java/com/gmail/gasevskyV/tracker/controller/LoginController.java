//package com.gmail.gasevskyV.tracker.controller;
//
//import com.gmail.gasevskyV.tracker.entity.User;
//import com.gmail.gasevskyV.tracker.repository.UserRepo;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@AllArgsConstructor
////@RequestMapping("/login")
//public class LoginController {
//    private final UserRepo userRepo;
//
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
//}
