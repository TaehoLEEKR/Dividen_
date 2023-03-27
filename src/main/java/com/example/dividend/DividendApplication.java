package com.example.dividend;

import com.example.dividend.scraper.YahooFinanceScraper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DividendApplication {

//    @Value("${spring.jwt.secret}")
//    private static String test;
    public static void main(String[] args) {
        SpringApplication.run(DividendApplication.class, args);

    }

}
