package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {


    // TODO: 2023-4-20 遗留问题 1.通过controller访问静态页面，无法加载 
    // TODO: 2023-4-20  2.引入的vue页面加载报错 
    
  @GetMapping("/geth")
  @ResponseBody
    public  String index(){
        return "test";
    }
}
