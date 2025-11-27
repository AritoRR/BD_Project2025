package ru.ananev;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.ananev.repository")
@EnableJpaAuditing
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}