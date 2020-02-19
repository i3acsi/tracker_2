package com.gmail.gasevskyV.tracker.config;

import com.gmail.gasevskyV.tracker.config.oauth.AuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableWebSecurity //(debug = true)
@Configuration
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthProvider authProvider;


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
                .and().logout().permitAll()
                .and().csrf().disable()
        ;

    }
}
