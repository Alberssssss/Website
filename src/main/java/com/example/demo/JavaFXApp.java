package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.WebApplicationType;

public class JavaFXApp extends Application {
    @Override
    public void init() throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AppLauncher.class)
            .web(WebApplicationType.NONE)
            .run(getParameters().getRaw().toArray(new String[0]));
        AppContext.setContext(context);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(AppContext.getContext()::getBean);
        Parent root = loader.load();
        stage.setTitle("成绩管理系统");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        AppContext.getContext().close();
        Platform.exit();
    }
}
