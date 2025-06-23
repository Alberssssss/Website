package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model) {
        // 这里可以注入服务拿动态数据，例如公告、课程等
        // model.addAttribute("announcements", announcementService.findAll());
        return "index";
    }
}