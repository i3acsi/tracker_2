package com.gmail.gasevskyV.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegController {
    @GetMapping("/signup")
    public String regPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String addUser(){
        return "redirect:/login";
    }
}
