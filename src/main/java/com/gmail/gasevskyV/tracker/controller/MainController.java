package com.gmail.gasevskyV.tracker.controller;

import com.gmail.gasevskyV.tracker.controller.util.ControllerUtils;
import com.gmail.gasevskyV.tracker.entity.Item;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.TrackItemRepo;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.LazyContextVariable;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@AllArgsConstructor
public class MainController {
    private final TrackItemRepo trackItemRepo;
    private final UserService userService;

    @RequestMapping(value = "/main", method = {RequestMethod.GET})
    public String main(
            @AuthenticationPrincipal User user,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            Model model) {
        loadListOfItems(model, userService.getCurrentUser(user, oAuth2User));
        return "main";
    }

    @RequestMapping(value = "/main", method = {RequestMethod.POST})
    public String addItem(
            @AuthenticationPrincipal User user,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @Valid Item item,
            BindingResult bindingResult,
            Model model) {
        User currentUser = userService.getCurrentUser(user, oAuth2User);
        item.setAuthor(currentUser);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            errorsMap.forEach((k,v)-> {
                model.addAttribute(k,v);
            });
            model.addAttribute("item", item);
        } else {
            item.setCreated(new Date());
            item.setUpdated(new Date());
            item.setActive(true);

            trackItemRepo.save(item);
            model.addAttribute("item", null);
        }
        loadListOfItems(model, currentUser);

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

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public String error() {
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
