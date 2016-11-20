package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.QuestionBank;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Elias Mera on 11/7/2016.
 */
public class MainController {
    private ImportExportUI mImportExportUI;
    private Generate mGenerate;
    private Grade mGrade;
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;

    @FXML
    VBox mainImExContainer;

    @FXML
    VBox mainGenContainer;


    /**
     * Initializer of MainController
     */
    public MainController() {
        this.mImportExportUI = new ImportExportUI(this);
        this.mQuestionBank = AppState.loadQuestionBank();
        this.mExamBank = AppState.loadExamBank();
        this.mGenerate = new Generate(this);
        this.mGrade = new Grade();
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

    /**
     * importTabSelected
     *
     * Callback for when the ImportExportTab is selected by the user
     *
     * @param event
     */
    public void importTabSelected(Event event) {
        mImportExportUI.loadImportExport(mainImExContainer);
    }

    public QuestionBank getQuestionBank() {
        return mQuestionBank;
    }

    public void setQuestionBank(QuestionBank questionBank) {
        mQuestionBank = questionBank;
        // Persist any changes to the current QuestionBank
        AppState.saveQuestionBank(mQuestionBank);
    }

    public ExamBank getExamBank() {
        return mExamBank;
    }

    @FXML
    JFXComboBox cbTemaG;
    @FXML
    JFXComboBox cbGrupoG;
    @FXML
    JFXListView lvExamenes;
    public void gradeTabSelected(Event event) throws IOException {

        if(mExamBank.getExams().isEmpty()){
            cbTemaG.setDisable(true);
            cbGrupoG.setDisable(true);
        }
        else{
            cbTemaG.setDisable(false);
            cbGrupoG.setDisable(false);
            cbTemaG.getSelectionModel().selectFirst();
            HashMap<String, ArrayList<Exam>> mExams = mExamBank.getExams();
            Iterator it = mExams.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry e = (Map.Entry)it.next();
                if(!cbTemaG.getItems().contains(e.getKey().toString()))
                    cbTemaG.getItems().add(e.getKey().toString());
            }
        }
    }

    /**
     * Method called when the user selected a topic to grade
     * @param actionEvent
     */
    public void selToGrade(ActionEvent actionEvent) {
        cbGrupoG.getItems().removeAll();
        lvExamenes.getItems().removeAll();
        cbGrupoG.getSelectionModel().selectFirst();
        String subject = cbTemaG.getValue().toString();
        ArrayList<Exam> exams = mExamBank.getExams(subject);
        for(Exam e : exams){
            if(!cbGrupoG.getItems().contains(e.getGroup()))
                cbGrupoG.getItems().add(e.getGroup());
        }
    }

    /**
     * Method called when the user selected a group
     * @param actionEvent
     */
    public void selGroup(ActionEvent actionEvent) {
        lvExamenes.getItems().removeAll();
        String subject = cbTemaG.getValue().toString();
        ArrayList<Exam> exams = mExamBank.getExams(subject);
        for(Exam e : exams){
            if(e.getGroup().equals(cbGrupoG.getValue().toString())){
                // TODO use an identifier to add exams to the list
            }
        }
    }
}
