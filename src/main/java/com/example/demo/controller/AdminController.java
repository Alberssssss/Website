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

import jakarta.servlet.http.HttpSession;
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
    public String adminPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        model.addAttribute("adminWelcome", "管理员，欢迎！");
        List<Announcement> all = announcementRepo.findAll();
        model.addAttribute("announcements", all);

        List<User> students = userRepo.findByRole(Role.STUDENT);
        List<User> teachers = userRepo.findByRole(Role.TEACHER);
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);

        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "admin-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        return userRepo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password) && u.getRole() == Role.ADMIN)
                .map(u -> {
                    session.setAttribute("isAdmin", true);
                    return "redirect:/admin";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "用户名或密码错误");
                    return "admin-login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("isAdmin");
        return "redirect:/login";
    }

    /**
     * 发布新公告
     */
    @PostMapping("/post")
    public String postAnnouncement(@RequestParam("content") String content,
                                   HttpSession session,
                                   Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("error", "公告内容不能为空");
        } else {
            announcementRepo.save(new Announcement(content.trim()));
            model.addAttribute("success", "公告已发布");
        }
        return adminPage(session, model);
    }

    @PostMapping("/create-teacher")
    public String createTeacher(@RequestParam String username,
                                @RequestParam String fullName,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }
        if (username == null || username.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            model.addAttribute("error", "请填写完整教师信息");
            return adminPage(session, model);
        }
        if (userRepo.findByUsername(username.trim()).isPresent()) {
            model.addAttribute("error", "用户名已存在");
            return adminPage(session, model);
        }
        userRepo.save(new User(username.trim(), password, Role.TEACHER, fullName.trim()));
        model.addAttribute("success", "老师账号已创建");
        return adminPage(session, model);
    }

    private boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("isAdmin"));
    }
}
