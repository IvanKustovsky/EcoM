package com.example.labs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    public static Stage innitialStage;
    @Override
    public void start(Stage stage) throws IOException {
        innitialStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pollution.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 936, 671);
        stage.setTitle("Забруднення");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
