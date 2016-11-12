package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import javafx.event.ActionEvent;

/**
 * Created by Elias Mera on 11/7/2016.
 * TODO(irvel): Initialize the application model at load
 */
public class MainController {
    private Import mImport;
    private Export mExport;
    private Generate mGenerate;
    private QuestionBank mQuestionBank;

    /**
     * Initializer of MainController
     */
    public MainController() {
        this.mImport = new Import();
        this.mQuestionBank = mImport.loadQuestionBank();
        this.mExport = new Export();
        this.mGenerate = new Generate();
    }

    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void importAction(ActionEvent actionEvent) {
        mQuestionBank = mImport.onClick(actionEvent, mQuestionBank);
    }

    /**
     * The user clicked the export button
     * @param actionEvent
     */
    public void exportAction(ActionEvent actionEvent) {
        mExport.onClick(actionEvent, mQuestionBank);
    }

    /**
     * The user clicked the generate button
     * @param actionEvent
     */
    public void generateAction(ActionEvent actionEvent) {
        mExport.onClick(actionEvent, mQuestionBank);
    }
}
