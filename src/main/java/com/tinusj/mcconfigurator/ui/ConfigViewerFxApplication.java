package com.tinusj.mcconfigurator.ui;

import com.tinusj.mcconfigurator.McConfiguratorApplication;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigViewerFxApplication extends Application {

    @Getter
    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(McConfiguratorApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        MainWindowController controller = applicationContext.getBean(MainWindowController.class);
        Scene scene = new Scene(controller.getRoot(), 1000, 600);

        primaryStage.setTitle("MC Configurator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }
}