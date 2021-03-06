package com.gmail.gasevskyV.tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/signup").setViewName("signup");
        registry.addViewController("/loginIN").setViewName("loginIN");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        registry.addViewController("/login").setViewName("login");

    }

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
