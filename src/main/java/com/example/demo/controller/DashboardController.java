package com.example.demo.controller;

import com.example.demo.model.Announcement;
import com.example.demo.model.Grade;
import com.example.demo.model.User;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@SessionAttributes("currentUser")  // 假设登录时把 User 放入了 session
public class DashboardController {

    @Autowired
    private AnnouncementRepository announcementRepo;

    @Autowired
    private GradeRepository gradeRepo;

    /**
     * 在登录后，需要把当前用户放到 Session 中。例如 LoginController 登录成功后：
     *   redirectAttributes.addFlashAttribute("currentUser", user);
     * 这里使用 @SessionAttributes("currentUser") 来保持它在会话里。
     */

    @ModelAttribute("currentUser")
    public User currentUser() {
        // 如果 session 中没有 currentUser，会调用此方法返回 null，
        // 实际项目中可以抛异常或跳转到登录页。
        return null;
    }

    /**
     * GET /dashboard
     * 显示用户仪表盘，包含欢迎语、公告列表、当前用户的成绩
     */
    @GetMapping
    public String showDashboard(@ModelAttribute("currentUser") User user, Model model) {
        // 欢迎语
        model.addAttribute("welcome", "欢迎, " + user.getUsername() + "！");
        // 公告列表
        List<Announcement> anns = announcementRepo.findAll();
        model.addAttribute("announcements", anns);
        // 该用户的成绩列表
        List<Grade> grades = gradeRepo.findAll().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        model.addAttribute("grades", grades);
        return "dashboard";  // 对应 templates/dashboard.html
    }

    /**
     * POST /dashboard/upload
     * 处理成绩上传表单
     */
    @PostMapping("/upload")
    public String uploadGrade(@RequestParam("course") String course,
                              @RequestParam("score") String scoreText,
                              @ModelAttribute("currentUser") User user,
                              Model model) {
        // 重用 GET 方法的模型数据
        showDashboard(user, model);

        // 验证输入
        if (course == null || course.trim().isEmpty() ||
                scoreText == null || scoreText.trim().isEmpty()) {
            model.addAttribute("error", "课程和成绩不能为空");
            return "dashboard";
        }
        try {
            Double score = Double.parseDouble(scoreText.trim());
            gradeRepo.save(new Grade(user, course.trim(), score));
            model.addAttribute("success", "成绩已上传！");
        } catch (NumberFormatException e) {
            model.addAttribute("error", "成绩请输入数字");
        }

        // 刷新成绩列表
        List<Grade> grades = gradeRepo.findAll().stream()
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        model.addAttribute("grades", grades);

        return "dashboard";
    }
}
