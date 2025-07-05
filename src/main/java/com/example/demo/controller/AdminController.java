package com.example.demo.controller;

import com.example.demo.model.Announcement;
import com.example.demo.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AnnouncementRepository announcementRepo;

    /**
     * 显示管理员公告页面
     */
    @GetMapping
    public String adminPage(Model model) {
        // 欢迎信息（可从 SecurityContext 或 Session 拿到当前用户）
        model.addAttribute("adminWelcome", "管理员，欢迎！");
        // 加载公告列表
        List<Announcement> all = announcementRepo.findAll();
        model.addAttribute("announcements", all);
        return "admin";  // 渲染 src/main/resources/templates/admin.html
    }

    /**
     * 发布新公告
     */
    @PostMapping("/post")
    public String postAnnouncement(@RequestParam("content") String content,
                                   Model model) {
        // 基本校验
        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("error", "公告内容不能为空");
        } else {
            announcementRepo.save(new Announcement(content.trim()));
            model.addAttribute("success", "公告已发布");
        }
        // 重新加载公告列表并返回页面
        List<Announcement> all = announcementRepo.findAll();
        model.addAttribute("announcements", all);
        model.addAttribute("adminWelcome", "管理员，欢迎！");
        return "admin";
    }
}
