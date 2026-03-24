package com.vcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IamCiamApplication {
    public static void main(String[] args) {
        SpringApplication.run(IamCiamApplication.class, args);
    }
}
