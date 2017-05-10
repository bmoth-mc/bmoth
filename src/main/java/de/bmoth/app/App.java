package de.bmoth.app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    PersonalPreference personalPreference;
    AppController appController = new AppController();

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


    /**
     * Load a given file into the CodeArea and change the title of the stage.
     *
     * @param file File to read from
     */
    private static String openFile(File file) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void main(String[] args) {
        launch(args);
    }

}

