package com.example.demo;

import com.example.demo.controller.LoginController;  // ← 导入 LoginController
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
    public void init() {
        // 启动 Spring 上下文（不抛 Exception 也没关系，捕获过就行）
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AppLauncher.class)
                .web(WebApplicationType.SERVLET)
                .run(getParameters().getRaw().toArray(new String[0]));
        AppContext.setContext(context);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 创建 FXMLLoader，并指定 ControllerFactory
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(type -> AppContext.getContext().getBean(type));

        // 加载根节点
        Parent root = loader.load();

        // 强制类型转换，或者你也可以使用 FXMLLoader<LoginController> loader = new FXMLLoader<>();
        LoginController loginCtrl = (LoginController) loader.getController();
        loginCtrl.setStage(stage);

        stage.setTitle("成绩管理系统");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() {
        // 优雅关闭 Spring 上下文和 JavaFX
        AppContext.getContext().close();
        Platform.exit();
    }
}
