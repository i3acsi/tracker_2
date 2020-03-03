package com.gmail.gasevskyV.tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration

public class MailConfig {
    @Value("${spring.mail.protocol}")
    private String protocol;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttls;

    @Value("${mail.smtp.debug}")
    private boolean debug;


    @Bean
    public JavaMailSender getSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();



        mailSender.setPort(587);
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol(protocol);

        Properties mailProperties = mailSender.getJavaMailProperties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", starttls);
        mailProperties.put("mail.smtp.debug", debug);

        return mailSender;
    }

}
