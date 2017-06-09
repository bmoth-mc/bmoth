package de.bmoth.app;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ViewTuple<AppView, AppViewModel> viewTuple = FluentViewLoader.fxmlView(AppView.class).load();
        Parent root = viewTuple.getView();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(App.class.getResource("keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        viewTuple.getCodeBehind().setupStage(primaryStage);
        primaryStage.show();
    }

}

