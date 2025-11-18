package com.tinusj.mcconfigurator;

import com.tinusj.mcconfigurator.ui.ConfigViewerFxApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McConfiguratorApplication {

    public static void main(String[] args) {
        javafx.application.Application.launch(ConfigViewerFxApplication.class, args);
    }

}
