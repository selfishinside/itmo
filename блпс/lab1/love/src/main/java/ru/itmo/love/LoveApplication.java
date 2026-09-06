package ru.itmo.love;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// точка входа приложения
@SpringBootApplication
@EnableScheduling
public class LoveApplication {

    // запускает spring контекст
    public static void main(String[] args) {
        SpringApplication.run(LoveApplication.class, args);
    }

}
