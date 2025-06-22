package com.example.demo;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppLauncher {
    public static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }
}
