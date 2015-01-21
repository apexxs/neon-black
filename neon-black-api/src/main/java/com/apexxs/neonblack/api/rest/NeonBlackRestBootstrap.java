package com.apexxs.neonblack.api.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class NeonBlackRestBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(NeonBlackRestBootstrap.class, args);
    }

}