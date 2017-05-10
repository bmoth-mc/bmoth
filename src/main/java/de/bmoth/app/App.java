package de.bmoth.app;

import java.io.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    PersonalPreference personalPreference;
    private AppController appController = new AppController();

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(App.class.getResource("keywords.css").toExternalForm());

        personalPreference = PersonalPreference.loadPreferenceFromFile();

        appController.setupStage(primaryStage);
        appController.setupPersonalPreference(personalPreference);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

