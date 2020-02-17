package com.gmail.gasevskyV.tracker.service;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private  UserRepo userRepo;
    private  MailSender mailSender;
    private  PasswordEncoder encoder;

    public UserService(){}

    @Autowired
    public UserService(UserRepo userRepo, MailSender mailSender, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public User loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email);
    }

    public User saveUser(User user){
        return userRepo.save(user);
    }

    public boolean addUser(User user){
        User userFromDB = userRepo.findByUsername(user.getUsername());
        if (userFromDB!=null){
            return false;
        }
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);
        if (!StringUtils.isEmpty(user.getEmail())){
            String message = String.format("Hello, %s!\nWelcome to Tracker system.\nPlease visit next link to activate your " +
                    "account:\nhttp://localhost:8084/activate/%s", user.getUsername(), user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);

        }
        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findUserByActivationCode(code);
        if (user==null){return false;}
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }


    public User oauth(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User userFromDB = userRepo.findByEmail(email);
        if (userFromDB==null){
            String googleId = oAuth2User.getAttribute("sub");
            String googleName = oAuth2User.getAttribute("name");
            String userpic = oAuth2User.getAttribute("picture");
            String gender = oAuth2User.getAttribute("gender");
            String locale = oAuth2User.getAttribute("locale");

            String firstName = oAuth2User.getAttribute("given_name");
            String surName = oAuth2User.getAttribute("family_name");

            User user = new User();
            user.setUsername(firstName);
            user.setSurname(surName);
            user.setActive(true);
            user.setEmail(email);
            user.setPassword("$2y$12$9LfMJxZo6meaapDwG6vxvufMCB73NgqTHebpOmLVTyrCiN00auRzS");
            user.setRoles(Collections.singleton(Role.ROLE_USER));

            user.setGoogleId(googleId);
            user.setGoogleName(googleName);
            user.setUserpic(userpic);
            user.setGender(gender);
            user.setLocale(locale);
            user.setLastVisit(LocalDateTime.now());
            userRepo.save(user);
            userFromDB = user;
        }

        return userFromDB;
    }
}
