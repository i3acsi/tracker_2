package com.gmail.gasevskyV.tracker.config.oauth;

import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
//@AllArgsConstructor
@Slf4j
public class AuthProvider implements AuthenticationProvider {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    @Autowired
    public AuthProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        User user;
        if (validate(username)) {
            user = (User) userService.loadUserByEmail(username);
        } else {
            user = (User) userService.loadUserByUsername(username);
        }

        if (user != null && (validate(username)&&((user.getUsername().equals(username))||(user.getEmail().equals(username))))) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Wrong password");
                throw new BadCredentialsException("Wrong password");
            }

            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

            return new UsernamePasswordAuthenticationToken(user, password, authorities);
        } else
            log.warn("Username not found");
            throw new BadCredentialsException("Username not found");
    }

    public boolean supports(Class<?> arg) {
        return true;
    }
}
