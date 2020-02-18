package com.gmail.gasevskyV.tracker.config;

import com.gmail.gasevskyV.tracker.config.oauth.AuthProvider;
import com.gmail.gasevskyV.tracker.entity.Role;
import com.gmail.gasevskyV.tracker.entity.User;
import com.gmail.gasevskyV.tracker.repository.UserRepo;
import com.gmail.gasevskyV.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;

@EnableWebSecurity //(debug = true)
@Configuration
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private UserRepo userRepo;


    @Qualifier("oauth2ClientContext")
    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/greeting", "/signup", "/activate/*", "/login", "/loginIN")
                        .permitAll()
//                    .antMatchers("/user/**")
//                        .hasRole("ADMIN")
                    .anyRequest()
                        .authenticated()
                .and()
                    .formLogin()
                        .loginPage("/loginIN")
                            .permitAll()
                                .successForwardUrl("/success")
                .and()
                    .oauth2Login()
                        .defaultSuccessUrl("/oauth")
//                .and()
//                .oauth2Client(oauth2Client ->
//                        oauth2Client.configure()
//                                )
                .and().logout().permitAll()
                .and().csrf().disable()
        ;

    }

//    @Configuration
//    @EnableGlobalMethodSecurity(prePostEnabled = true)
//    public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//        @Override
//        protected MethodSecurityExpressionHandler createExpressionHandler() {
//            return new OAuth2MethodSecurityExpressionHandler();
//        }
//    }


//
//    @Bean
//    public PrincipalExtractor principalExtractor(UserService userService) {
//        return map -> {
//            String email = (String) map.get("email");
//            User userFromDB = userService.loadUserByEmail(email);
//            if (userFromDB == null) {
//                String googleId = (String) map.get("sub");
//                String googleName = (String) map.get("name");
//                String userpic = (String) map.get("picture");
//                String gender = (String) map.get("gender");
//                String locale = (String) map.get("locale");
//
//                String firstName = (String) map.get("given_name");
//                String surName = (String) map.get("family_name");
//
//                userFromDB = new User();
//                userFromDB.setUsername(firstName);
//                userFromDB.setSurname(surName);
//                userFromDB.setActive(true);
//                userFromDB.setEmail(email);
//                userFromDB.setPassword("$2y$12$9LfMJxZo6meaapDwG6vxvufMCB73NgqTHebpOmLVTyrCiN00auRzS");
//                userFromDB.setRoles(Collections.singleton(Role.ROLE_USER));
//
//                userFromDB.setGoogleId(googleId);
//                userFromDB.setGoogleName(googleName);
//                userFromDB.setUserpic(userpic);
//                userFromDB.setGender(gender);
//                userFromDB.setLocale(locale);
//            }
//            userFromDB.setLastVisit(LocalDateTime.now());
//            return userService.saveUser(userFromDB);
//        };
//    }

}
/*

 */


