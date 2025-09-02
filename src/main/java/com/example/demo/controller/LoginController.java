package com.example.demo.controller;

import com.example.demo.model.LoginRecord;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    // 显示登录页
    @GetMapping("/login")
    public String loginForm() {
        return "login";        // 对应 src/main/resources/templates/login.html
    }

    // 处理登录提交
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String role,
                          Model model) {
        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            model.addAttribute("error", "请输入完整信息");
            loginRecordRepository.save(new LoginRecord(username, false));
            return "login";
        }
        Role reqRole;
        try {
            reqRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "角色选择无效");
            loginRecordRepository.save(new LoginRecord(username, false));
            return "login";
        }
        Role finalReqRole = reqRole;
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password) && u.getRole() == finalReqRole)
                .map(user -> {
                    loginRecordRepository.save(new LoginRecord(username, true));
                    if (finalReqRole == Role.ADMIN) return "redirect:/admin";
                    if (finalReqRole == Role.TEACHER) return "redirect:/teacher";
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    loginRecordRepository.save(new LoginRecord(username, false));
                    model.addAttribute("error", "用户名、密码或角色不正确");
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
