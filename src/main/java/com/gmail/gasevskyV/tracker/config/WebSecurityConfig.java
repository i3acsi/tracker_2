package com.gmail.gasevskyV.tracker.config;

import com.gmail.gasevskyV.tracker.config.oauth.AuthProvider;
import com.gmail.gasevskyV.tracker.service.UserService;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import javax.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
    private  AuthProvider authProvider;
//    @Autowired
    private  UserService userService;

//    @Autowired
    @Qualifier("oauth2ClientContext")
    private OAuth2ClientContext oAuth2ClientContext;


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/greeting", "/signup", "/activate/*").permitAll()
                .antMatchers("/user/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                    .and()
                .formLogin()
                .loginPage("/loginIN").permitAll()
                .successForwardUrl("/success")
                    .and().
                oauth2Login().loginPage("/login").permitAll()
                    .and()
                .logout().permitAll()
        ;
        http
                .addFilterBefore(ssoFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oAuth2ClientFilterRegistration(OAuth2ClientContextFilter oAuth2ClientContextFilter)
    {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(oAuth2ClientContextFilter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter ssoFilter()
    {
        OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/login");
        OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(google(), oAuth2ClientContext);
        googleFilter.setRestTemplate(googleTemplate);
        CustomUserInfoTokenServices tokenServices = new CustomUserInfoTokenServices(googleResource().getUserInfoUri(), google().getClientId());
        tokenServices.setRestTemplate(googleTemplate);
        googleFilter.setTokenServices(tokenServices);
        tokenServices.setUserRepo(userRepo);
        tokenServices.setPasswordEncoder(passwordEncoder);
        return googleFilter;
    }
}
