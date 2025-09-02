package com.example.demo.controller;

import com.example.demo.config.VersionInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final VersionInfo versionInfo;
    // 如果你有公告服务，也可以同时注入：
    // private final AnnouncementService announcementService;

    public WebController(VersionInfo versionInfo /*, AnnouncementService announcementService */) {
        this.versionInfo = versionInfo;
        // this.announcementService = announcementService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // 把版本号和构建时间放到模板里
        model.addAttribute("appVersion",   versionInfo.getVersion());
        model.addAttribute("buildTime",    versionInfo.getBuildTime());

        // 如果你有公告，也可以这样：
        // model.addAttribute("announcements", announcementService.findAll());

        return "index";
    }
}