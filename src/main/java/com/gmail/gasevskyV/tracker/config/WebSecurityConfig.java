package com.gmail.gasevskyV.tracker.config;

import com.gmail.gasevskyV.tracker.config.oauth.AuthProvider;
import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@EnableWebSecurity //(debug = true)

//@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        private AuthProvider authProvider;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/", "/greeting", "/signup", "/activate/*", "/login", "/loginIN").permitAll()
                    .antMatchers("/user/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and().formLogin()
                    .loginPage("/loginIN").permitAll()
                    .successForwardUrl("/success")
                    .and().logout().permitAll()
                    .and().csrf().disable()
            ;

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authProvider);
        }
    }

    @Configuration
    @EnableOAuth2Client
    @Order(2)
    static class OAuth2SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Bean
        public PrincipalExtractor principalExtractor(UserService userService) {
            return map -> {
                String email = (String) map.get("email");
                User userFromDB = userService.loadUserByEmail(email);
                if (userFromDB == null){
                    String googleId = (String) map.get("sub");
                    String googleName = (String) map.get("name");
                    String userpic = (String) map.get("picture");
                    String gender = (String) map.get("gender");
                    String locale = (String) map.get("locale");

                    String firstName =(String) map.get("given_name");
                    String surName = (String) map.get("family_name");

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
                return userService.saveUser(userFromDB);
            };
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/", "/greeting", "/signup", "/activate/*", "/login", "/loginIN").permitAll()
                    .antMatchers("/user/**").hasRole("ADMIN")
                    .anyRequest().authenticated()

                    .and().oauth2Login().defaultSuccessUrl("/oauth")

                    .and().logout().permitAll()
                    .and().csrf().disable()
            ;

        }

    }
}



