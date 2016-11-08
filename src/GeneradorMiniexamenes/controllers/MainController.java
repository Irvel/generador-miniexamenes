package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import javafx.event.ActionEvent;

/**
 * Created by Elias Mera on 11/7/2016.
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
        this.mExport = new Export();
        this.mGenerate = new Generate();
    }

    /**
     * The user clicked import
     * @param actionEvent
     */
    public void importAction(ActionEvent actionEvent) {
        mImport.onClick(actionEvent);
    }

    public void generateAction(ActionEvent actionEvent){

    }
}
