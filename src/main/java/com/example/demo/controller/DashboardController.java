package com.example.demo.controller;

import com.example.demo.model.Announcement;
import com.example.demo.model.Grade;
import com.example.demo.model.User;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
@Component @Scope("prototype")
public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private TextArea announcementsArea;
    @FXML private TextArea gradesArea;
    @FXML private TextField courseField;
    @FXML private TextField scoreField;
    @FXML private Label statusLabel;
    @Autowired private AnnouncementRepository announcementRepo;
    @Autowired private GradeRepository gradeRepo;

    private Stage stage;
    private User user;

    public void setStage(Stage stage) { this.stage = stage; }
    public void setUser(User user) { this.user = user; }

    public void onLoaded() {
        welcomeLabel.setText("欢迎, " + user.getUsername() + "!");
        List<Announcement> anns = announcementRepo.findAll();
        String annText = anns.isEmpty() ? "（暂无公告）" :
            anns.stream().map(a -> "• " + a.getContent() + " (" + a.getCreatedTime() + ")")
                .collect(Collectors.joining("\n"));
        announcementsArea.setText(annText);

        List<Grade> grades = gradeRepo.findAll().stream()
            .filter(g -> g.getUser().getId().equals(user.getId()))
            .collect(Collectors.toList());
        String gradeText = grades.isEmpty() ? "（尚未上传成绩）" :
            grades.stream().map(g -> g.getCourse() + ": " + g.getScore())
                .collect(Collectors.joining("\n"));
        gradesArea.setText(gradeText);
    }

    @FXML
    private void handleUploadGrade() {
        String course = courseField.getText().trim();
        String scoreTxt = scoreField.getText().trim();
        if (course.isEmpty() || scoreTxt.isEmpty()) {
            statusLabel.setText("课程和成绩不能为空");
            return;
        }
        try {
            Double score = Double.parseDouble(scoreTxt);
            Grade grade = new Grade(user, course, score);
            gradeRepo.save(grade);
            statusLabel.setText("成绩已上传!");
            onLoaded();
            courseField.clear();
            scoreField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("成绩请输入数字");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(AppContext.getContext()::getBean);
            Parent root = loader.load();
            LoginController ctrl = loader.getController();
            ctrl.setStage(stage);
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
