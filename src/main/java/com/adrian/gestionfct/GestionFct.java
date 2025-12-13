package com.adrian.gestionfct;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.adrian.gestionfct.config.StageManager;
import com.adrian.gestionfct.view.FxmlView;

import javafx.application.Application;
import javafx.stage.Stage;

@SpringBootApplication
public class GestionFct extends Application {

    protected ConfigurableApplicationContext springContext;
    protected StageManager stageManager;

    @Override
    public void init() throws Exception {
        springContext = springBootApplicationContext();
    }

    public static void main(final String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stageManager = springContext.getBean(StageManager.class, primaryStage);
        displayInitialScene();
    }

    protected void displayInitialScene() {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    private ConfigurableApplicationContext springBootApplicationContext() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(GestionFct.class);
        String[] args = getParameters().getRaw().stream().toArray(String[]::new);
        return builder.run(args);
    }
}
