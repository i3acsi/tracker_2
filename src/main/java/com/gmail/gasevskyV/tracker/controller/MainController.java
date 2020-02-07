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
import org.thymeleaf.context.LazyContextVariable;

import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class MainController {
    private final TrackItemRepo trackItemRepo;

    @RequestMapping(value = "/main", method = {RequestMethod.GET})
    public String main(
            @AuthenticationPrincipal User user,
            Model model) {
        loadListOfItems(model, user);
        return "main";
    }

    @RequestMapping(value = "/main", method = {RequestMethod.POST})
    public String addItem(
            @AuthenticationPrincipal User user,
            @RequestParam String task,
            @RequestParam String description,
            Model model) {
        Item item = new Item(task, description, user);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        item.setActive(true);

        trackItemRepo.save(item);

        loadListOfItems(model, user);

        return ("main");
    }


    @RequestMapping(value = "/success", method = {RequestMethod.GET, RequestMethod.POST})
    public String redirect() {
        return "redirect:/main";
    }

    private void loadListOfItems(Model model, User user) {
        user.getRoles().forEach(role -> {
            if (role.name().equals("ROLE_USER")) {
                List<Item> list = trackItemRepo.findAllByActive(true);
                if (!list.isEmpty()) {
                    model.addAttribute("itemList", list);
                }
            }
            if (role.name().equals("ROLE_ADMIN")) {
                model.addAttribute("isAdmin", true);
            }
        });
    }

    @RequestMapping(value = "/error" , method = {RequestMethod.GET, RequestMethod.POST})
    public String error(){
        return "redirect:/main";
    }

    private void lazyLoad(Model model) {
        model.addAttribute("listItem",
                new LazyContextVariable<List<Item>>() {
                    @Override
                    protected List<Item> loadValue() {
                        return trackItemRepo.findAllByActive(true);
                    }
                }
        );
    }
}
