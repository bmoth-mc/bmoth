package de.bmoth.app;

import java.io.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private AppController appController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("app.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(App.class.getResource("keywords.css").toExternalForm());

        PersonalPreference personalPreference = PersonalPreference.loadPreferenceFromFile();

        appController = loader.getController();
        appController.setupStage(primaryStage);
        appController.setupPersonalPreference(personalPreference);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

