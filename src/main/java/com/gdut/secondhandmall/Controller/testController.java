package com.gdut.secondhandmall.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class testController {

    @GetMapping("/testHttp")
    public String testHttp(String code){

        return "访问成功，部署成功"+code;
    }
}
