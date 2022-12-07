package com.moviedb_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MoviedbApiNosqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviedbApiNosqlApplication.class, args);
    }

}
