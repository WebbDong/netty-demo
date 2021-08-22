package com.webbdong.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Webb Dong
 * @date 2021-08-20 4:06 PM
 */
@SpringBootApplication(scanBasePackages = {"com.webbdong"})
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}
