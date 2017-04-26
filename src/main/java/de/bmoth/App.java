package de.bmoth;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
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

        MenuItem save = new MenuItem("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveFile();
            }
        });

        MenuItem saveAs = new MenuItem("Save As");
        saveAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveFileAS();
            }
        });

        menuFile.getItems().addAll(open,save,saveAs, new SeparatorMenuItem(), exit);
        menuBar.getMenus().addAll(menuFile, menuCheck);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                });
        codeArea.setPrefSize(800,600);
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

    private static void openFile(Stage stage,CodeArea codeArea)  {
        FileChooser fileChooser= new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open MCH File", "*.mch"));
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file=fileChooser.showOpenDialog(stage);

        if(file!=null){
            String content = null;
            try {
                content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{codeArea.clear();}catch (Exception e){
                e.printStackTrace();
            }
            codeArea.appendText(content);
        }
    }
    private void saveFileAS() {
    }

    private void saveFile() {
    }

    public static void main(String[] args){
    	launch(args);
    }
}
