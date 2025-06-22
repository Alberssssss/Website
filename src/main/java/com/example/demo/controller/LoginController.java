package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component @Scope("prototype")
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @Autowired private UserRepository userRepository;

    private Stage stage;
    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    private void initialize() { messageLabel.setText(""); }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("请输入用户名和密码");
            return;
        }
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            if (user.getPassword().equals(password)) {
                if (user.isAdmin()) openAdminDashboard(user);
                else openUserDashboard(user);
            } else messageLabel.setText("密码错误");
        }, () -> messageLabel.setText("用户不存在"));
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            loader.setControllerFactory(AppContext.getContext()::getBean);
            Parent root = loader.load();
            RegisterController ctrl = loader.getController();
            ctrl.setStage(stage);
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openUserDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            loader.setControllerFactory(AppContext.getContext()::getBean);
            Parent root = loader.load();
            DashboardController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setUser(user);
            ctrl.onLoaded();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openAdminDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
            loader.setControllerFactory(AppContext.getContext()::getBean);
            Parent root = loader.load();
            AdminController ctrl = loader.getController();
            ctrl.setStage(stage);
            ctrl.setUser(user);
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
