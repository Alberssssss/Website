package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // 用来在模板中绑定表单对象（可选）
        model.addAttribute("userForm", new User());
        return "register";    // 对应 src/main/resources/templates/register.html
    }

    /**
     * 处理注册提交
     */
    @PostMapping("/register")
    public String doRegister(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("confirm")  String confirm,
                             Model model) {
        // 基本校验
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                confirm == null || confirm.isEmpty()) {
            model.addAttribute("error", "请填写所有字段");
            return "register";
        }
        if (!password.equals(confirm)) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "register";
        }
        if (userRepository.findByUsername(username.trim()).isPresent()) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }

        // 判断是否第一个注册用户 → 管理员
        boolean isAdmin = (userRepository.count() == 0);
        User user = new User(username.trim(), password, isAdmin);
        userRepository.save(user);

        // 注册成功后跳转到登录页，并带上提示
        model.addAttribute("message",
                isAdmin ? "注册成功！(管理员创建)，请登录" : "注册成功！现在可以登录");
        return "login";    // 对应登录模板 login.html
    }
}
