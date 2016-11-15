package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.QuestionBank;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Created by Elias Mera on 11/7/2016.
 */
public class MainController {
    private Import mImport;
    private Export mExport;
    private Generate mGenerate;
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;

    @FXML
    VBox mainGenContainer;

    /**
     * Initializer of MainController
     */
    public MainController() {
        this.mImport = new Import();
        this.mQuestionBank = AppState.loadQuestionBank();
        this.mExamBank = AppState.loadExamBank();
        this.mExport = new Export();
        this.mGenerate = new Generate(this);
    }

    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void importAction(ActionEvent actionEvent) {
        mQuestionBank = mImport.onClick(actionEvent, mQuestionBank);
        AppState.saveQuestionBank(mQuestionBank);
    }

    /**
     * The user clicked the export button
     * @param actionEvent
     */
    public void exportAction(ActionEvent actionEvent) {
        mExport.onClick(actionEvent, mQuestionBank);
    }

    /**
     * generateTabSelected
     *
     * Callback for when the GenerateTab is selected by the user
     *
     * @param event
     */
    public void generateTabSelected(Event event) {
        mGenerate.loadGenerateForm(mainGenContainer);
    }

    public QuestionBank getQuestionBank() {
        return mQuestionBank;
    }

    public ExamBank getExamBank() {
        return mExamBank;
    }
}
