package com.example.demo.controller;

import com.example.demo.model.Announcement;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AnnouncementRepository announcementRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * 显示管理员公告页面
     */
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("adminWelcome", "管理员，欢迎！");
        List<Announcement> all = announcementRepo.findAll();
        model.addAttribute("announcements", all);

        List<User> students = userRepo.findByRole(Role.STUDENT);
        List<User> teachers = userRepo.findByRole(Role.TEACHER);
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        return "admin";
    }

    /**
     * 发布新公告
     */
    @PostMapping("/post")
    public String postAnnouncement(@RequestParam("content") String content,
                                   Model model) {
        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("error", "公告内容不能为空");
        } else {
            announcementRepo.save(new Announcement(content.trim()));
            model.addAttribute("success", "公告已发布");
        }
        return adminPage(model);
    }

    @PostMapping("/create-teacher")
    public String createTeacher(@RequestParam String username,
                                @RequestParam String fullName,
                                @RequestParam String password,
                                Model model) {
        if (username == null || username.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            model.addAttribute("error", "请填写完整教师信息");
            return adminPage(model);
        }
        if (userRepo.findByUsername(username.trim()).isPresent()) {
            model.addAttribute("error", "用户名已存在");
            return adminPage(model);
        }
        userRepo.save(new User(username.trim(), password, Role.TEACHER, fullName.trim()));
        model.addAttribute("success", "老师账号已创建");
        return adminPage(model);
    }
}
