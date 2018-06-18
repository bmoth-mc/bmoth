package de.bmoth.app;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.microsoft.z3.Status;
import de.bmoth.checkers.initialstateexists.InitialStateExistsChecker;
import de.bmoth.checkers.initialstateexists.InitialStateExistsCheckingResult;
import de.bmoth.checkers.invariantsatisfiability.InvariantSatisfiabilityChecker;
import de.bmoth.checkers.invariantsatisfiability.InvariantSatisfiabilityCheckingResult;
import de.bmoth.eventbus.ErrorEvent;
import de.bmoth.eventbus.EventBusProvider;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.modelchecker.bmc.BoundedModelChecker;
import de.bmoth.modelchecker.esmc.ExplicitStateModelChecker;
import de.bmoth.modelchecker.kind.KInductionModelChecker;
import de.bmoth.parser.Parser;
import de.bmoth.parser.ParserException;
import de.bmoth.parser.ast.nodes.MachineNode;
import de.bmoth.preferences.BMothPreferences;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Julian on 09.06.2017.
 */
public class AppView implements FxmlView<AppViewModel>, Initializable {

    private static final String APPNAME = "Bmoth";
    private static final int MAX_STEPS = 20; //shouldn't be hardcoded
    private final Logger logger = Logger.getLogger(getClass().getName());
    @FXML
    MenuItem kInductCheck;
    @FXML
    MenuItem boundedCheck;
    @FXML
    MenuItem newFile;
    @FXML
    MenuItem open;
    @FXML
    MenuItem save;
    @FXML
    MenuItem saveAs;
    @FXML
    MenuItem options;
    @FXML
    MenuItem presentation;
    @FXML
    MenuItem exit;
    @FXML
    MenuItem modelCheck;
    @FXML
    CodeArea codeArea;
    @FXML
    TextArea infoArea;
    @FXML
    TextArea warningArea;
    @InjectViewModel
    private AppViewModel appViewModel;
    private Stage primaryStage = new Stage();
    private String currentFile;
    private Boolean hasChanged = false;
    private Task<ModelCheckingResult> task;
    private ModelChecker modelChecker;
    private Thread modelCheckingThread;
    private MachineNode machineNode;
    private Boolean presentationMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY));
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_ANY));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_ANY));
        warningArea.setWrapText(true);
        presentation.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_ANY));
        codeArea.selectRange(0, 0);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
            .subscribe(change -> codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText())));
        codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText()));
        codeArea.textProperty().addListener((observableValue, s, t1) -> {
            hasChanged = true;
            infoArea.setText("Unsaved changes");
        });
        appViewModel.codeProperty().bind(codeArea.textProperty());
        warningArea.textProperty().bind(appViewModel.warningsProperty());
        EventBusProvider.getInstance();
        EventBusProvider.getInstance().getEventBus().register(this);

    }

    void setupStage(Stage stage) {
        primaryStage = stage;
        setupPersonalPreferences();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleExit();
        });
    }

    void setupPersonalPreferences() {
        if (!BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_FILE).isEmpty()) {
            currentFile = BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_FILE);
            if (new File(currentFile).exists()) {
                File file = new File(currentFile);
                String fileContent = openFile(file);
                codeArea.replaceText(fileContent);
                primaryStage.setTitle(APPNAME + " - " + file.getName().substring(0, file.getName().length() - 4));
            }
            codeArea.getUndoManager().forgetHistory();

        }
    }

    @FXML
    public void handleNew() {
        int nextStep = -1;
        if (hasChanged) {
            nextStep = handleUnsavedChanges();
        }
        if (nextStep != 0) {
            codeArea.replaceText("");
            codeArea.getUndoManager().forgetHistory();
            codeArea.selectRange(0, 0);
            currentFile = null;
            hasChanged = false;
            primaryStage.setTitle(APPNAME + " - " + "New Machine");
            infoArea.clear();
        }
    }

    @FXML
    public void handleOpen() {
        int nextStep = -1;
        if (hasChanged) {
            nextStep = handleUnsavedChanges();
        }
        if (nextStep == -1 || !hasChanged) {
            String fileContent = openFileChooser();
            if (fileContent != null) {
                codeArea.replaceText(fileContent);
                codeArea.getUndoManager().forgetHistory();
                codeArea.selectRange(0, 0);
                hasChanged = false;
                machineNode = null;
                warningArea.clear();
                infoArea.clear();
            }
        }
    }

    @FXML
    public void handleSave() {
        if (currentFile != null) {
            try {
                saveFile(currentFile);
                hasChanged = false;
                infoArea.clear();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "While Saving", e);
            }
        } else {
            handleSaveAs();
        }
    }

    @FXML
    public Boolean handleSaveAs() {
        try {
            Boolean saved = saveFileAs();
            if (saved) {
                hasChanged = false;
                infoArea.clear();
            }
            return saved;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "While Saving AS", e);
        }
        return false;
    }

    @FXML
    public void handleExit() {
        if (hasChanged) {
            int nextStep = handleUnsavedChanges();
            if (nextStep == -1 || !hasChanged) {
                Platform.exit();
            }

        } else {
            Platform.exit();
        }
    }

    @FXML
    public void handleOptions() throws IOException {
        ViewTuple<OptionView, OptionViewModel> viewOptionViewModelViewTuple = FluentViewLoader
            .fxmlView(OptionView.class).load();
        Parent root = viewOptionViewModelViewTuple.getView();
        Scene scene = new Scene(root);
        Stage optionStage = new Stage();
        optionStage.setScene(scene);
        optionStage.show();
    }

    @FXML
    public void handleCheck() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {

            parseMachineNode();

            modelChecker = new ExplicitStateModelChecker(machineNode);

            checkWithChecker();

        }
    }

    public void handleBounded() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {

            parseMachineNode();

            modelChecker = new BoundedModelChecker(machineNode, MAX_STEPS);

            checkWithChecker();
        }


    }

    public void handleKInduct() {

        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {

            parseMachineNode();

            modelChecker = new KInductionModelChecker(machineNode, MAX_STEPS);

            checkWithChecker();
        }

    }

    public void checkWithChecker() {

        task = new Task<ModelCheckingResult>() {
            @Override
            protected ModelCheckingResult call() throws Exception {
                return AppView.this.modelChecker.check();
            }
        };

        task.setOnSucceeded(event -> {
            ModelCheckingResult result = task.getValue();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Model Checking Result");

            switch (result.getType()) {
                case VERIFIED:
                    alert.setHeaderText("The model is...");
                    alert.setContentText("...VERIFIED by " + modelChecker.getClass().getSimpleName() + "!\nNo counter-example found.");
                    break;
                case EXCEEDED_MAX_STEPS:
                    alert.setHeaderText("The model is...");
                    alert.setContentText("...correct for " + result.getSteps() + " steps!\n(Warning: Exceeded max steps)");
                    break;
                case COUNTER_EXAMPLE_FOUND:
                    alert.setHeaderText("The model is...");
                    alert.setContentText(
                        "...not correct!\nCounter-example found in state " + result.getLastState().toString()
                            + ".\nReversed path: " + result.getCounterExamplePath());
                    break;
                case LTL_COUNTER_EXAMPLE_FOUND:
                    alert.setHeaderText("The LTL-model is...");
                    alert.setContentText(
                        "...not correct!\nCounter-example found in state " + result.getLastState().toString()
                            + ".\nReversed path: " + result.getCounterExamplePath());
                    break;

                case UNKNOWN:
                    alert.setHeaderText("Model checking result unknown...");
                    alert.setContentText(
                        "... reason: " + result.getReason());
                    break;
                case ABORTED:
                    alert.setHeaderText("Model checking aborted...");
                    alert.setContentText(
                        "... after " + result.getSteps() + " steps");
                    break;
            }

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        });
        task.setOnCancelled(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Model Checking Result");
            alert.setHeaderText("Model checking was canceled!");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        });
        modelCheckingThread = new Thread(task);
        modelCheckingThread.start();
    }


    private void parseMachineNode() {
        if (hasChanged || machineNode == null) {
            handleSave();
            try {
                machineNode = Parser.getMachineAsSemanticAst(codeArea.getText());
            } catch (ParserException e) {
                EventBus eventBus = EventBusProvider.getInstance().getEventBus();
                eventBus.post(new ErrorEvent("Syntax error", e.toString(), e));
                return;
            }
            if (!machineNode.getWarnings().isEmpty()) {
                warningArea.setText(machineNode.getWarnings().toString());
            }
        }
    }

    @FXML
    public void handelCancelModelCheck(ActionEvent actionEvent) {
        task.cancel();
        modelCheckingThread.interrupt();
    }

    @FXML
    public void handleInvariantSatisfiability() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {
            parseMachineNode();
            InvariantSatisfiabilityCheckingResult result = InvariantSatisfiabilityChecker
                .doInvariantSatisfiabilityCheck(machineNode);
            showResultAlert(result.getResult());
        }
    }

    @FXML
    public void handleREPL() throws IOException {

        ViewTuple<ReplView, ReplViewModel> viewReplViewModelViewTuple = FluentViewLoader.fxmlView(ReplView.class).load();
        Parent root = viewReplViewModelViewTuple.getView();
        Scene scene = new Scene(root);
        Stage replStage = new Stage();
        replStage.setTitle("REPL");
        replStage.setScene(scene);
        replStage.show();
    }
    // </editor-fold>

    private int handleUnsavedChanges() {
        int nextStep = saveChangedDialog();
        switch (nextStep) {
            case 0:
                break;
            case 1:
                handleSave();
                break;
            case 2:
                handleSaveAs();
                break;
            case -1:
                break;
            default:
                break;
        }
        return nextStep;
    }

    /**
     * Open a confirmation-alert to decide how to proceed with unsaved changes.
     *
     * @return UserChoice as Integer: -1 = Ignore, 0 = Cancel, 1 = Save , 2 =
     * SaveAs
     */
    private int saveChangedDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("UNSAVED CHANGES!");
        alert.setHeaderText("Unsaved Changes! What do you want to do?");
        alert.setContentText(null);

        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeSaveAs = new ButtonType("Save As");
        ButtonType buttonTypeIgnoreChanges = new ButtonType("Ignore");
        ButtonType buttonTypeCancel = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeSaveAs, buttonTypeIgnoreChanges, buttonTypeCancel);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeSave)
                return 1;
            if (result.get() == buttonTypeSaveAs)
                return 2;
            if (result.get() == buttonTypeCancel)
                return 0;
            if (result.get() == buttonTypeIgnoreChanges)
                return -1;
        }
        return 0;
    }

    // <editor-fold desc="File operations">

    /**
     * Save codeArea to a file.
     *
     * @param path Save-location
     * @throws IOException
     */
    private void saveFile(String path) throws IOException {
        File file = new File(path);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(codeArea.getText());
            currentFile = file.getAbsolutePath();
            primaryStage.setTitle(APPNAME + " - " + file.getName().substring(0, file.getName().length() - 4));
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_FILE, file.getAbsolutePath());
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_DIR, file.getParent());

        }
    }

    /**
     * Ask for location and name and save codeArea.
     *
     * @throws IOException
     * @see #saveFile(String)
     */
    private Boolean saveFileAs() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
            new File(BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_DIR)));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MCH File", "*.mch"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) { // add .mch ending if not added by OS
            if (!file.getAbsolutePath().endsWith(".mch")) {
                saveFile(file.getAbsolutePath() + ".mch");
            } else {
                saveFile(file.getAbsolutePath());
            }
            currentFile = file.getAbsolutePath();
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_FILE, file.getAbsolutePath());
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_DIR, file.getParent());
            return true;
        } else
            return false;
    }

    /**
     * Ask the user which file to open into the textarea. If the file is found,
     * openFile is called.
     *
     * @return Returns the filepath as string or null if cancelled
     * @see #openFile(File)
     */
    private String openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open MCH File", "*.mch"));
        fileChooser.setTitle("Choose File");
        File f = new File(BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_DIR));
        fileChooser.setInitialDirectory(f.exists() && f.isDirectory() ? f : new File("."));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            currentFile = file.getPath();
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_FILE, file.getAbsolutePath());
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_DIR, file.getParent());
            primaryStage.setTitle(APPNAME + " - " + file.getName().substring(0, file.getName().length() - 4));
            return openFile(file);
        }
        return null;
    }

    /**
     * Load a given file into the CodeArea and change the title of the stage.
     *
     * @param file File to read from
     */
    private String openFile(File file) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "While Reading File", e);
        }
        return content;
    }
    // </editor-fold>

    // <editor-fold desc="Event Bus Subscriptions">
    @Subscribe
    public void showException(ErrorEvent event) {
        new ErrorAlert(Alert.AlertType.ERROR, event.getErrorType(), event.getMessage());
    }
    // </editor-fold>

    public void handleInitialStateExists() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {
            if (hasChanged || machineNode == null) {
                handleSave();
                try {
                    machineNode = Parser.getMachineAsSemanticAst(codeArea.getText());
                } catch (ParserException e) {
                    EventBus eventBus = EventBusProvider.getInstance().getEventBus();
                    eventBus.post(new ErrorEvent("Parse error", e.toString(), e));
                    return;
                }
            }

            InitialStateExistsCheckingResult result = InitialStateExistsChecker.doInitialStateExistsCheck(machineNode);
            showResultAlert(result.getResult());
        }
    }

    private void showResultAlert(Status status) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invariant Satisfiability Checking Result");
        alert.setHeaderText("Initial state...");
        switch (status) {
            case UNSATISFIABLE:
                alert.setContentText("...does not exists!\nThe model is probably not correct.");
                break;
            case UNKNOWN:
                alert.setContentText("...is unknown!\nThe initialization is too complex for the backend.");
                break;
            case SATISFIABLE:
                alert.setContentText("...exists!");
                break;
            default:
                throw new IllegalArgumentException("Unhandled result: " + status.toString());
        }

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

    public void handlePresentation(ActionEvent actionEvent) {
        if (!presentationMode) {
            codeArea.setStyle("-fx-font-size:25");
            presentationMode = true;
        } else {
            codeArea.setStyle("-fx-font-size:15");
            presentationMode = false;
        }

    }

}
