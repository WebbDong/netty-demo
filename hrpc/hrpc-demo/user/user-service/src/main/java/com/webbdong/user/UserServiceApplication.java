package com.webbdong.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Webb Dong
 * @date 2021-08-19 8:55 PM
 */
@SpringBootApplication(scanBasePackages = {"com.webbdong"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
