package main.java.ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "main.java.ru.practicum",
        "stats.client"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}