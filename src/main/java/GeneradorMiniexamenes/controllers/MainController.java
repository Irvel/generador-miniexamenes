package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.Options;
import GeneradorMiniexamenes.model.QuestionBank;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * MainController
 *
 * This class is the main view controller for the application. It is responsible for creating
 * and holding a reference to all the sub-controllers. It is also responsible for holding the
 * variables that are needed by every sub-controller, and makes them available through getters.
 */
public class MainController {
    // Model of the application
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private Options mOptions;

    // Sub-controllers for each tab
    private GenerateExamsController mGenerateExamsController;
    @FXML private ViewExamsController viewExamsTabController;
    @FXML private ImportExportController importExportTabController;
    @FXML private GradeExamsController gradeExamsTabController;
    @FXML private OptionsController optionsTabController;


    @FXML private VBox mainGenContainer;

    /**
     * Initializer of MainController
     */
    public MainController() {
        mQuestionBank = AppState.loadQuestionBank(AppState.QUESTIONS_PATH);
        mExamBank = AppState.loadExamBank(AppState.EXAMS_PATH);
        mOptions = AppState.loadOptions(AppState.OPTIONS_PATH);
        this.mGenerateExamsController = new GenerateExamsController(mQuestionBank,
                                                                    mExamBank,
                                                                    mOptions);
    }

    @FXML private void initialize() {
        this.gradeExamsTabController.setModel(mExamBank);
        this.importExportTabController.setModel(mQuestionBank);
        this.viewExamsTabController.setModel(mExamBank, mGenerateExamsController);
        this.optionsTabController.setModel(mOptions);
    }
    /**
     * generateTabSelected
     *
     * Callback for when the GenerateTab is selected by the user
     *
     * @param event
     */
    public void generateTabSelected(Event event) {
        mGenerateExamsController.loadGenerateForm(mainGenContainer);
    }

    /**
     * importTabSelected
     *
     * Callback for when the ImportExportTab is selected by the user
     *
     * @param event
     */
    public void importTabSelected(Event event) {
        this.importExportTabController.resetExportOptions();
    }

    public void gradeTabSelected(Event event) throws IOException {
        gradeExamsTabController.tabSelected();
    }

    public void viewExamsTabSelected(Event event) {
        this.viewExamsTabController.loadViewExamsForm();
    }

    public void optionsTabSelected(Event event) {
        this.optionsTabController.loadForm();
    }
}
