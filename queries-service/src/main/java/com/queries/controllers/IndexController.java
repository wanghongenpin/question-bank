package com.queries.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
