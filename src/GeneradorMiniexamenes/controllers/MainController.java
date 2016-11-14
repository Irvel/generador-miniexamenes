package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

/**
 * Created by Elias Mera on 11/7/2016.
 */
public class MainController {
    private Import mImport;
    private Export mExport;
    private Generate mGenerate;
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private boolean bInicio = true;
    private String sSelectedSubject = "";
    private String sSelectedGroup = "";
    @FXML JFXComboBox cbTema;
    @FXML Spinner spCantidad;
    @FXML JFXButton btnGenerate;
    @FXML JFXTextField tfGrupo;

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
     * method that returns the subject selected by the user
     * @return selected Subject
     */
    private Subject findSubject(){
        for(Subject s : mQuestionBank.getSubjects())
            if(s.getSubjectName().equals(sSelectedSubject))
                return s;
        return null;
    }

    /**
     * The user clicked the generate button
     * @param actionEvent
     */
    public void generateAction(ActionEvent actionEvent) {
        // finds the subject
        Subject subject = findSubject();

        int iQuantity = Integer.parseInt(spCantidad.getValue().toString());
        mExamBank = mGenerate.onClick(actionEvent, subject, iQuantity, tfGrupo.getText());
        tfGrupo.setText("");
        spCantidad.decrement(100);

        // checks exams
        /*
        int iC = 1;
        for(Exam e : mExamBank.getExams(subject.getSubjectName())){
            System.out.println("Examen numero " + iC++);
            System.out.println(e.getSubject());
            System.out.println(e.getGroup());
            for(Question q : e.getQuestions()){
                System.out.println("Pregunta " + q.getQuestion());
            }
            System.out.println();
            System.out.println();
        }*/

        // Saves the generated exam.
        if(mExamBank != null) {
            AppState.saveExamBank(mExamBank);
        }
    }

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
                if (!cbTema.getItems().contains(s.getSubjectName())) {
                    cbTema.getItems().add(s.getSubjectName());
                }
            }
            cbTema.getSelectionModel().selectFirst();
        }
    }

    /**
     * Method that updates Subject
     * @param actionEvent
     */
    public void cambioTema(ActionEvent actionEvent) {
        sSelectedSubject = cbTema.getValue().toString();
    }
}
