package de.bmoth;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuCheck = new Menu("Checks");
        CodeArea codeArea = new CodeArea();

        MenuItem open = new MenuItem("Open");
        open.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                openFile(primaryStage,codeArea);
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });

        menuFile.getItems().addAll(open, new SeparatorMenuItem(), exit);
        menuBar.getMenus().addAll(menuFile, menuCheck);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                });

        Scene scene = new Scene(new VBox(), 800, 600);

        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, codeArea);

        primaryStage.setScene(scene);
        primaryStage.setTitle("BMoth");
        primaryStage.show();
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        spansBuilder.add(Collections.emptyList(), text.length());
        return spansBuilder.create();
    }

    private static void openFile(Stage stage,CodeArea codeArea) {
        FileChooser fileChooser= new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open MCH File", "*.mch"));
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file=fileChooser.showOpenDialog(stage);
        if(file!=null){
            codeArea.appendText(file.getAbsolutePath());
        }
    }

    public static void main(String[] args){
    	launch(args);
    }
}
