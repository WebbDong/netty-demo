package com.webbdong.product.controller;

import com.webbdong.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Webb Dong
 * @date 2021-08-21 7:59 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping("/getProduct")
    public String getProduct() {
        return productService.getProduct();
    }

}
