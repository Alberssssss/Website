package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // 显示登录页
    @GetMapping("/login")
    public String loginForm() {
        return "login";        // 对应 src/main/resources/templates/login.html
    }

    // 处理登录提交
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          Model model) {
        if (username.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "请输入用户名和密码");
            return "login";
        }
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .map(user -> {
                    if (user.isAdmin()) return "redirect:/admin";
                    else return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "用户名或密码错误");
                    return "login";
                });
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout() {
        // 这里可以清空 Session 等
        return "redirect:/login";
    }
}
