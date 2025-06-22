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
public class RegisterController {
    @FXML private TextField newUsernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @Autowired private UserRepository userRepository;

    private Stage stage;
    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    private void initialize() { messageLabel.setText(""); }

    @FXML
    private void handleRegister() {
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("请填写所有字段");
            return;
        }
        if (!password.equals(confirm)) {
            messageLabel.setText("两次输入的密码不一致");
            return;
        }
        if (userRepository.findByUsername(username).isPresent()) {
            messageLabel.setText("用户名已存在");
            return;
        }
        boolean isAdmin = userRepository.count() == 0; 
        User user = new User(username, password, isAdmin);
        userRepository.save(user);
        messageLabel.setText(isAdmin ? "注册成功！(管理员创建)" : "注册成功！现在可以登录");
    }

    @FXML
    private void goToLogin() {
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
