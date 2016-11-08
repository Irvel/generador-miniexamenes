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

    public MainController() {
        this.mImport = new Import();
        this.mExport = new Export();
        this.mGenerate = new Generate();
    }

    public void importAction(ActionEvent actionEvent) {
        mImport.onClick(actionEvent);
    }

    public void exportAction(ActionEvent actionEvent) {
        mExport.onClick(actionEvent, mQuestionBank);
    }
}
