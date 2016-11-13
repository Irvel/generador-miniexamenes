package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.QuestionBank;
import GeneradorMiniexamenes.model.Subject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * Created by Elias Mera on 11/7/2016.
 * TODO(irvel): Initialize the application model at load
 */
public class MainController {
    private Import mImport;
    private Export mExport;
    private Generate mGenerate;
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private boolean bInicio = true;

    /**
     * Initializer of MainController
     */
    public MainController() {
        this.mImport = new Import();
        this.mQuestionBank = AppState.loadQuestionBank();
        this.mExamBank = AppState.loadExamBank();
        this.mExport = new Export();
        this.mGenerate = new Generate();
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
     * The user clicked the generate button
     * @param actionEvent
     */
    public void generateAction(ActionEvent actionEvent) {
        //mExport.onClick(actionEvent, mQuestionBank);
        // Save the generated exams to storage
        AppState.saveExamBank(mExamBank);
    }

    @FXML JFXComboBox cbTema;
    @FXML JFXComboBox cbCantidad;
    @FXML JFXButton btnGenerate;

    /**
     * tabGenera
     *
     * Method that loads subjects every time the user clicks on tabGeneraExamen
     * Disables Genera button if there are no subjects
     *
     * @param event
     */
    public void tabGenera(Event event) {
        // Checks if there is at least one topic
        if(mQuestionBank.getSubjects().isEmpty()){
            btnGenerate.setDisable(true);
        }
        else{
            btnGenerate.setDisable(false);
            // Loads subjects
            for(Subject s : mQuestionBank.getSubjects()) {
                if (!cbTema.getItems().contains(s.getSubjectName()))
                    cbTema.getItems().add(s.getSubjectName());
            }

            // loads quantities only on start
            if(bInicio) {
                for (int i = 1; i <= 35; i++)
                    cbCantidad.getItems().add(i);
                bInicio = !bInicio;
            }
        }
    }
}
