package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elias Mera on 11/7/2016.
 */
public class MainController {
    private ImportExportUI mImportExportUI;
    private Generate mGenerate;
    private Grade mGrade;
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private boolean mSubjectListenerActive;
    private boolean mGroupListenerActive;

    @FXML
    VBox mainImExContainer;

    @FXML
    VBox mainGenContainer;


    /**
     * Initializer of MainController
     */
    public MainController() {
        mImportExportUI = new ImportExportUI(this);
        mQuestionBank = AppState.loadQuestionBank();
        mExamBank = AppState.loadExamBank();
        mGenerate = new Generate(this);
        mGrade = new Grade();
        mSubjectListenerActive = false;
        mGroupListenerActive = false;
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
    JFXComboBox cbExamenesG;
    @FXML
    JFXComboBox cbQuestionG;
    @FXML
    JFXComboBox cbAnswerG;
    @FXML
    JFXTextField tfCalif;
    @FXML
    Spinner spPond;
    @FXML
    JFXTextField tfPond;
    @FXML
    AnchorPane viewExamsContainer;
    private int[] arrPond;
    private JFXListView mListView;
    /**
     * resetGradeFields
     *
     * Basically dump the ExamBank into the GradeView. Specifically, it resets the grade exams form
     * fields with the available subjects, groups and exams from the ExamBank.
     *
     */
    private void resetGradeFields(Boolean subjectWasSelected, Boolean groupWasSelected) {
        if (!subjectWasSelected) {
            // Disable the listener on the combobox to avoid a callback loop
            mSubjectListenerActive = false;
            // selecting the first subject
            // Fill the subject combobox and select the first one
            cbTemaG.getItems().clear();
            for (HashMap.Entry<String, ArrayList<Group>> subject : mExamBank.getGroups()
                                                                            .entrySet()) {
                cbTemaG.getItems().add(subject.getKey());
            }
            cbTemaG.getSelectionModel().selectFirst();

            // After reseting the subject, the group needs to be reset as well
            groupWasSelected = false;
        }

        if (!groupWasSelected) {
            mGroupListenerActive = false;
            // Fill the group combobox with the selected subject groups and select the first one
            cbGrupoG.getItems().clear();
            for (Group group : mExamBank.getGroups(cbTemaG.getValue().toString())) {
                cbGrupoG.getItems().add(group.getGroupName());
            }
            cbGrupoG.getSelectionModel().selectFirst();
        }
        if (mListView != null) {
            viewExamsContainer.getChildren().remove(mListView);
        }
        mListView = new JFXListView<String>();
        mListView.getSelectionModel().selectedItemProperty().addListener(mExamListListener);
        ArrayList<Exam> exams = mExamBank.getExams(cbTemaG.getValue().toString(),
                                                   cbGrupoG.getValue().toString());
        for (Exam exam : exams) {
            mListView.getItems().add("Examen número " + exam.getExamNumber());
        }
        mListView.getStyleClass().add("mylistview");
        viewExamsContainer.getChildren().add(mListView);
        AnchorPane.setLeftAnchor(mListView, 0.0);
        AnchorPane.setRightAnchor(mListView, 0.0);
        AnchorPane.setTopAnchor(mListView, 0.0);
        AnchorPane.setBottomAnchor(mListView, 0.0);
        mSubjectListenerActive = true;
        mGroupListenerActive = true;
    }

    // TODO: Move this to somewhere clean
    private ChangeListener mSubjectListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            if (mSubjectListenerActive) {
                System.out.println("A subject has been selected " + ov.getValue().toString());
                resetGradeFields(true, false);
            }
        }
    };
    // TODO: Move this as well
    private ChangeListener mGroupListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            if (mGroupListenerActive) {
                System.out.println("A group as been selected " + ov.getValue().toString());
                resetGradeFields(true, true);
            }
        }
    };
    // TODO: Aaaand this as well
    private ChangeListener mExamListListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            if (mGroupListenerActive) {
                System.out.println("An exam has been selected " + ov.getValue().toString());
                System.out.println(mListView.getSelectionModel().getSelectedItem().toString());
            }
        }
    };

    boolean fistTime = true;
    public void gradeTabSelected(Event event) throws IOException {
        if(mExamBank.getGroups().isEmpty()){
            cbTemaG.setDisable(true);
            cbGrupoG.setDisable(true);
            cbExamenesG.setDisable(true);
            cbQuestionG.setDisable(true);
            cbAnswerG.setDisable(true);
        }
        else if(fistTime){
            fistTime = false;
            cbTemaG.setDisable(false);
            cbGrupoG.setDisable(false);
            cbExamenesG.setDisable(false);
            cbQuestionG.setDisable(false);
            cbAnswerG.setDisable(false);
            resetGradeFields(false, false);
            cbTemaG.getSelectionModel().selectedItemProperty().addListener(mSubjectListener);
            cbGrupoG.getSelectionModel().selectedItemProperty().addListener(mGroupListener);
        }
        else {
            resetGradeFields(false, false);
        }
    }

    /**
     * Method called when the user selected a topic to grade
     * @param actionEvent
     */
    public void selSubjectGrade(ActionEvent actionEvent) {
        resetGradeFields(true, false);
    }

    /**
     * Method called when the user selected a group
     * @param actionEvent
     */
    public void selGroup(ActionEvent actionEvent) {
        resetGradeFields(true, true);
    }

    public void selExam(ActionEvent actionEvent) {
        cbQuestionG.getItems().clear();
        cbAnswerG.getItems().clear();
        tfCalif.setText("");
        String subject = cbTemaG.getValue().toString();
        String group = cbGrupoG.getValue().toString();
        ArrayList<Exam> exams = mExamBank.getExams(subject, group);
        Exam exam = exams.get(Integer.parseInt(cbExamenesG.getValue().toString()) - 1);
        for(Question q : exam.getQuestions()) {
            if (!cbQuestionG.getItems().contains(q.getQuestion()))
                cbQuestionG.getItems().add(q.getQuestion());
        }
        System.out.println(Integer.parseInt(cbExamenesG.getValue().toString()) - 1);
        arrPond = new int[exam.getQuestions().size()];
        for(int i = 0; i < arrPond.length; i++)
            arrPond[i] = 0;
    }

    public void selQuestion(ActionEvent actionEvent){
        cbAnswerG.getItems().clear();
        String subject = cbTemaG.getValue().toString();
        String question = cbQuestionG.getValue().toString();
        String group = cbGrupoG.getValue().toString();
        ArrayList<Exam> exams = mExamBank.getExams(subject, group);
        Exam exam = exams.get(Integer.parseInt(cbExamenesG.getValue().toString()) - 1);
        for(Question q : exam.getQuestions()){
            if(q.getQuestion().equals(cbQuestionG.getValue().toString())){
                for(Answer a : q.getAnswers()){
                    if(!cbAnswerG.getItems().contains(a.getAnswer())){
                        cbAnswerG.getItems().add(a.getAnswer());
                    }
                }
            }
        }
    }

    public void selAnswer(ActionEvent actionEvent) {
        String subject = cbTemaG.getValue().toString();
        String question = cbQuestionG.getValue().toString();
        String group = cbGrupoG.getValue().toString();
        ArrayList<Exam> exams = mExamBank.getExams(subject, group);
        Exam exam = exams.get(Integer.parseInt(cbExamenesG.getValue().toString()) - 1);
        int iC = 0;
        for(Question q : exam.getQuestions()){
            if(q.getQuestion().equals(cbQuestionG.getValue().toString())){
                for(Answer a : q.getAnswers()){
                    if(cbAnswerG.getValue() != null && a.getAnswer().equals(cbAnswerG.getValue().toString())) {
                        arrPond[iC] = a.getWeight();
                        tfPond.setText(a.getWeight()+"");
                    }
                }
            }
            iC++;
        }

        int iSum = 0;
        for(int i = 0; i < exam.getQuestions().size(); i++)
            iSum += arrPond[i];
        tfCalif.setText((iSum*1.0/exam.getQuestions().size()) + "");
    }
}
