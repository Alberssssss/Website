package com.example.demo.controller;

import com.example.demo.model.Announcement;
import com.example.demo.model.User;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
@Component @Scope("prototype")
public class AdminController {
    @FXML private Label adminWelcomeLabel;
    @FXML private TextArea announcementContentArea;
    @FXML private TextArea allAnnouncementsArea;
    @FXML private Label statusLabel;
    @Autowired private AnnouncementRepository announcementRepo;

    private Stage stage;
    private User adminUser;

    public void setStage(Stage stage) { this.stage = stage; }
    public void setUser(User user) {
        this.adminUser = user;
        adminWelcomeLabel.setText("管理员 " + user.getUsername() + "，欢迎!");
        loadAnnouncements();
    }

    @FXML
    private void initialize() { statusLabel.setText(""); }

    @FXML
    private void handlePostAnnouncement() {
        String content = announcementContentArea.getText().trim();
        if (content.isEmpty()) {
            statusLabel.setText("公告内容不能为空");
            return;
        }
        Announcement ann = new Announcement(content);
        announcementRepo.save(ann);
        statusLabel.setText("公告已发布");
        announcementContentArea.clear();
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        List<Announcement> all = announcementRepo.findAll();
        if (all.isEmpty()) {
            allAnnouncementsArea.setText("（暂无公告）");
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String text = all.stream()
                .map(a -> "[" + a.getCreatedTime().format(fmt) + "] " + a.getContent())
                .collect(Collectors.joining("\n"));
            allAnnouncementsArea.setText(text);
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
