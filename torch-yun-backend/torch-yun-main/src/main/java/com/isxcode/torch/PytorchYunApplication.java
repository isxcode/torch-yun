package com.isxcode.torch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
@SpringBootApplication
public class PytorchYunApplication {

    public static void main(String[] args) {

        SpringApplication.run(PytorchYunApplication.class, args);
    }

    @RequestMapping(value = {"/*", "/home/**"})
    public String index() {

        return "index";
    }
}
