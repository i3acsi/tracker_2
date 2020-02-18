package com.gmail.gasevskyV.tracker.service;

import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
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
            user.setEmail(user.getEmail().toLowerCase());
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

    public boolean hasRoleAdmin(OAuth2User oAuth2User){
        User user = oauth(oAuth2User);
        AtomicBoolean result = new AtomicBoolean(false);
        user.getAuthorities().forEach(authority->{
            if (authority.equals(Role.ROLE_ADMIN)){
                result.set(true);
            }
        });
        return result.get();
    }

    public User oauth(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        email = email.toLowerCase();
        User userFromDB = userRepo.findByEmail(email);
        if (userFromDB==null){
            String googleId = oAuth2User.getAttribute("sub");
            String googleName = oAuth2User.getAttribute("name");
            String userpic = oAuth2User.getAttribute("picture");
            String gender = oAuth2User.getAttribute("gender");
            String locale = oAuth2User.getAttribute("locale");

            String firstName = oAuth2User.getAttribute("given_name");
            String surName = oAuth2User.getAttribute("family_name");

            userFromDB = new User();
            userFromDB.setUsername(firstName);
            userFromDB.setSurname(surName);
            userFromDB.setActive(true);
            userFromDB.setEmail(email);
            userFromDB.setPassword("$2y$12$9LfMJxZo6meaapDwG6vxvufMCB73NgqTHebpOmLVTyrCiN00auRzS");
            userFromDB.setRoles(Collections.singleton(Role.ROLE_USER));

            userFromDB.setGoogleId(googleId);
            userFromDB.setGoogleName(googleName);
            userFromDB.setUserpic(userpic);
            userFromDB.setGender(gender);
            userFromDB.setLocale(locale);
        }
        userFromDB.setLastVisit(LocalDateTime.now());
        userRepo.save(userFromDB);

        return userFromDB;
    }

    public User getCurrentUser(User user, OAuth2User oAuth2User){
        if (user!=null){
            return user;
        }
        if (oAuth2User!=null){
            return oauth(oAuth2User);
        }
        log.error("smth wrong user = null and oauthUser = null");
        return null;
    }
}
