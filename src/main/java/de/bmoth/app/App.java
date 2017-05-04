package de.bmoth.app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import de.bmoth.modelchecker.ModelChecker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.LineNumberFactory;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private String currentFile;
    private PersonalPreference personalPreference;
    private Boolean hasChanged;
    private final static String APPNAME="Bmoth";

    @Override
    public void start(Stage primaryStage) {
        personalPreference=PersonalPreference.loadPreferenceFromFile();
        currentFile=personalPreference.getLastFile();
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuCheck = new Menu("Checks");
        CodeArea codeArea = new CodeArea();

        if(personalPreference.getLastFile()!=null) {
            openFile(primaryStage, codeArea, new File(personalPreference.getLastFile()));
            codeArea.selectRange(0,0);
        }
        TextArea infoArea = new TextArea();
        MenuItem open = new MenuItem("Open");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        MenuItem modelCheck = new MenuItem("Start Modelchecking");
        hasChanged=false;



        open.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                int nextStep=-1;
                if (hasChanged){
                    nextStep=saveChangedDialog();
                    switch (nextStep){
                        case 0: break;
                        case 1: save.fire(); break;
                        case 2: saveAs.fire(); break;
                        case -1: break;
                    }
                }
                if(nextStep!=0) {
                    String s = openFileChooser(primaryStage, codeArea, personalPreference);
                    currentFile = s;
                    hasChanged = false;
                    infoArea.clear();
                    codeArea.selectRange(0, 0);
                }
            }
        });



        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                PersonalPreference.savePrefToFile(personalPreference);
                if (hasChanged){
                    int nextStep = saveChangedDialog();
                    switch (nextStep){
                        case 0: break;
                        case 1: save.fire(); Platform.exit(); break;
                        case 2: saveAs.fire(); Platform.exit(); break;
                        case -1: Platform.exit(); break;
                    }
                } else {
                    Platform.exit();
                }
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(currentFile!=null){
                    try {
                        saveFile(currentFile,codeArea);
                        hasChanged=false;
                        infoArea.clear();
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
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY));

        saveAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    saveFileAs(primaryStage,codeArea);
                    hasChanged=false;
                    infoArea.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        modelCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean hasModel;
                hasModel = ModelChecker.doModelCheck(codeArea.getText());
                Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
                if(hasModel){
                    alert.setContentText("Model Found");
                } else {
                    alert.setContentText("No Model Found");
                }
                alert.showAndWait();
            }
        });

        menuFile.getItems().addAll(open,save,saveAs, new SeparatorMenuItem(), exit);
        menuCheck.getItems().add(modelCheck);
        menuBar.getMenus().addAll(menuFile, menuCheck);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText()));
                });
        codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText()));             //Needed to apply Highlighting on last used File
        codeArea.setPrefSize(800,600);

        codeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                hasChanged=true;
                infoArea.setText("Unsaved Changes");
            }
        });
        infoArea.setEditable(false);
        infoArea.setPrefSize(0,0);


        Scene scene = new Scene(new VBox(), 800, 600);
        scene.getStylesheets().add(App.class.getResource("keywords.css").toExternalForm());
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, codeArea,infoArea);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                exit.fire();
            }
        });
        primaryStage.setScene(scene);
        if(primaryStage.getTitle()==null) primaryStage.setTitle(APPNAME);
        primaryStage.show();
    }

    /**
     * Opens a Confirmation-Alert to decide how to proceed with unsaved Changes
     *
     * @return UserChoice as Integer: -1 = Ignore, 0 = Cancel, 1 = Save , 2 = SaveAs
     */
    private int saveChangedDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("UNSAVED CHANGES!");
        alert.setHeaderText("Unsaved Changes! What do you want to do");
        alert.setContentText(null);

        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeSaveAs = new ButtonType("Save As");
        ButtonType buttonTypeIgnoreChanges = new ButtonType("Ignore Changes!");
        ButtonType buttonTypeCancel = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeSave,buttonTypeSaveAs,buttonTypeIgnoreChanges,buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == buttonTypeSave) return 1;
        if(result.get() == buttonTypeSaveAs) return 2;
        if(result.get() == buttonTypeCancel) return 0;
        if(result.get() == buttonTypeIgnoreChanges) return -1;
        return 0;
    }

    /**
     * Asking the user which File to open into a Textarea, if the file is found, openFile is called
     *@see #openFile(Stage, CodeArea, File)
     *
     * @param stage Stage to open Dialog
     * @param codeArea  Textarea which will display the Code
     * @param personalPreference Preference for Initialdirectory
     * @return Returns the filepath as String or null if canceled
     */
    private static String openFileChooser(Stage stage, CodeArea codeArea, PersonalPreference personalPreference)  {
        FileChooser fileChooser= new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open MCH File", "*.mch"));
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(new File(personalPreference.getPrefdir()));
        File file=fileChooser.showOpenDialog(stage);

        if(file!=null){
            personalPreference.setLastFile(file.getAbsolutePath());
            personalPreference.setPrefdir(file.getParent());
            openFile(stage,codeArea,file);
            return file.getAbsolutePath();

        }
        return null;
    }

    /**
     * Open a given File into the CodeArea
     * Change title of Stage
     *
     * @param stage     Stage for changing Title
     * @param codeArea  CodeArea to display code
     * @param file      File to read code
     */
    private static void openFile(Stage stage, CodeArea codeArea, File file) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        codeArea.replaceText(content);
        codeArea.deletehistory();
        stage.setTitle( APPNAME + " - " +file.getName());
    }

    /**
     * Saves Code into a File
     * @param path  Save-Location
     * @param codeArea  Codearea to read text from
     * @throws IOException
     */
    private static void saveFile(String path,CodeArea codeArea) throws IOException {
        File file = new File(path);
        if(!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter;
        fileWriter = new FileWriter(file);
        fileWriter.write(codeArea.getText());
        fileWriter.close();
    }

    /**
     * Asks for location and name and saves code
     * @see #saveFile(String, CodeArea)
     * @param stage
     * @param codeArea
     * @throws IOException
     */
    private static void saveFileAs(Stage stage, CodeArea codeArea) throws IOException{
        FileChooser fileChooser=new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MCH File","*.mch"));
        File file = fileChooser.showSaveDialog(stage);
        if(file!=null)      //add .mch ending if not added by OS
            if (!file.getAbsolutePath().endsWith(".mch")) {
                saveFile(file.getAbsolutePath() + ".mch", codeArea);
            } else {
            saveFile(file.getAbsolutePath(),codeArea);
            }
    }

    public static void main(String[] args){
    	launch(args);
    }
}
