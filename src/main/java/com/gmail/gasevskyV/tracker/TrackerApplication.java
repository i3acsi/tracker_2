package com.gmail.gasevskyV.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class TrackerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(TrackerApplication.class, args);
    }
}
