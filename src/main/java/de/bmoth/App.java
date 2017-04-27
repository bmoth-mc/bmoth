package de.bmoth;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import javafx.application.Platform;
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
    private String currentFile;

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuCheck = new Menu("Checks");
        CodeArea codeArea = new CodeArea();
        MenuItem open = new MenuItem("Open");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");




        open.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                String s=openFile(primaryStage,codeArea);
                currentFile=s;
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(currentFile!=null){
                    try {
                        saveFile(currentFile,codeArea);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    try {
                        saveFileAs(primaryStage,codeArea);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        saveAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    saveFileAs(primaryStage,codeArea);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private static String openFile(Stage stage,CodeArea codeArea)  {
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
            codeArea.replaceText(content);
            stage.setTitle("Bmoth - " +file.getName());


        }
        return file.getAbsolutePath();
    }

    private void saveFile(String path,CodeArea codeArea) throws IOException {
        File file = new File(path);
        if(!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = null;
        fileWriter = new FileWriter(file);
        fileWriter.write(codeArea.getText());
        fileWriter.close();
    }

    private void saveFileAs(Stage stage, CodeArea codeArea) throws IOException{
        FileChooser fileChooser=new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showSaveDialog(stage);
            saveFile(file.getAbsolutePath(),codeArea);

    }

    public static void main(String[] args){
    	launch(args);
    }
}
