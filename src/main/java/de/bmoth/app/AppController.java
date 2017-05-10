package de.bmoth.app;

import de.bmoth.modelchecker.ModelChecker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Jessy on 10.05.17.
 */
public class AppController implements Initializable {

    @FXML MenuItem open;
    @FXML MenuItem save;
    @FXML MenuItem saveAs;
    @FXML MenuItem exit;
    @FXML MenuItem modelCheck;

    @FXML CodeArea codeArea;
    @FXML TextArea infoArea;

    private Stage primaryStage = new Stage();
    private PersonalPreference personalPreference = new PersonalPreference();
    private String content = "";
    private static Boolean hasChanged = false;
    private final String APPNAME = "Bmoth";


    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY));

        codeArea.selectRange(0,0);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
            .subscribe(change -> {
                codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText()));
            });
        codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText()));

        if (personalPreference.getLastFile() != null) {
            content = personalPreference.getLastFile();
            String fileContent = openFile(new File(personalPreference.getLastFile()));
            codeArea.replaceText(fileContent);
            codeArea.deletehistory();
        }

        codeArea.textProperty().addListener((observableValue, s, t1) -> {
            hasChanged = true;
            infoArea.setText("Unsaved changes");
        });
    }

    void setupStage(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle(APPNAME);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleExit();
        });
    }

    void setupPersonalPreference(PersonalPreference preference) {
        personalPreference = preference;
    }

    @FXML
    public void handleOpen() {
        int nextStep = -1;
        if (hasChanged) {
            nextStep = saveChangedDialog();
            switch (nextStep) {
                case 0:
                    break;
                case 1:
                    save.fire();
                    break;
                case 2:
                    saveAs.fire();
                    break;
                case -1:
                    break;
            }
        }
        if (nextStep != 0) {
            String fileContent = openFileChooser();
            codeArea.selectRange(0, 0);
            codeArea.replaceText(fileContent);
            codeArea.deletehistory();
            hasChanged = false;
            infoArea.clear();
        }
    }

    @FXML
    public void handleSave() {
        if (content != null) {
            try {
                saveFile(content);
                hasChanged = false;
                infoArea.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                saveFileAs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleSaveAs() {
        try {
            saveFileAs();
            hasChanged = false;
            infoArea.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExit() {
        PersonalPreference.savePrefToFile(personalPreference);
        if (hasChanged) {
            int nextStep = saveChangedDialog();
            switch (nextStep) {
                case 0:
                    break;
                case 1:
                    save.fire();
                    Platform.exit();
                    break;
                case 2:
                    saveAs.fire();
                    Platform.exit();
                    break;
                case -1:
                    Platform.exit();
                    break;
            }
        } else {
            Platform.exit();
        }
    }

    @FXML
    public void handleCheck() {
        boolean noCounterExample;
        noCounterExample = ModelChecker.doModelCheck(codeArea.getText());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (noCounterExample) {
            alert.setContentText("No Counter-Example Found");
        } else {
            alert.setContentText("Counter-Example Found");
        }
        alert.showAndWait();
    }


    /**
     * Save codeArea to a file.
     *
     * @param path Save-location
     * @throws IOException
     */
    private void saveFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(codeArea.getText());
        fileWriter.close();
    }

    /**
     * Ask for location and name and save codeArea.
     *
     * @throws IOException
     * @see #saveFile(String)
     */
    private void saveFileAs() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MCH File", "*.mch"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null)      //add .mch ending if not added by OS
            if (!file.getAbsolutePath().endsWith(".mch")) {
                saveFile(file.getAbsolutePath() + ".mch");
            } else {
                saveFile(file.getAbsolutePath());
            }
    }

    /**
     * Open a confirmation-alert to decide how to proceed with unsaved changes.
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

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeSaveAs, buttonTypeIgnoreChanges, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeSave) return 1;
            if (result.get() == buttonTypeSaveAs) return 2;
            if (result.get() == buttonTypeCancel) return 0;
            if (result.get() == buttonTypeIgnoreChanges) return -1;
        }
        return 0;
    }

    /**
     * Ask the user which file to open into the textarea. If the file is found, openFile is called.
     *
     * @return Returns the filepath as string or null if cancelled
     * @see #openFile(File)
     */
    private String openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open MCH File", "*.mch"));
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(new File(personalPreference.getPrefdir()));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            personalPreference.setLastFile(file.getAbsolutePath());
            personalPreference.setPrefdir(file.getParent());
            String content = openFile(file);
            primaryStage.setTitle(APPNAME + " - " + file.getName());
            return content;
        }
        return null;
    }

    /**
     * Load a given file into the CodeArea and change the title of the stage.
     *
     * @param file     File to read from
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

}
