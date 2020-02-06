package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.entity.Item;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.TrackItemRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class MainController {
    private final TrackItemRepo trackItemRepo;

    @RequestMapping(value = "/main", method = {RequestMethod.POST})
    public String main(
            @AuthenticationPrincipal User user,
            Model model) {
        user.getRoles().forEach(role -> {
            if (role.name().equals("USER")) {
                List<Item> list = trackItemRepo.findAllByActive(true);
                if (!list.isEmpty()) {
                    model.addAttribute("itemList", list);
                }
            }
        });
        return "main";
    }

    @RequestMapping(value = "/main", method = {RequestMethod.GET})
    public String addItem(
            @AuthenticationPrincipal User user,
            @RequestParam String task,
            @RequestParam String description,
            Model model) {
        log.info("im here");
        Item item = new Item(task, description, user);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        item.setActive(true);

        trackItemRepo.save(item);

        user.getRoles().forEach(role -> {
            if (role.name().equals("USER")) {
                model.addAttribute("itemList", trackItemRepo.findAllByActive(true));
            }
        });

        return ("main");
    }
}
