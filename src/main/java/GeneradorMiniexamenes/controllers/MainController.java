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
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private boolean mBackgroundLight;
    private HashMap<Integer, Integer> mExamIdxToNumber;

    @FXML
    VBox mainImExContainer;
    @FXML
    VBox mainGenContainer;
    @FXML
    JFXComboBox cbTemaG;
    @FXML
    JFXComboBox cbGrupoG;
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
    @FXML
    ScrollPane viewQuestionsContainer;
    private int[] arrPond;
    private JFXListView mListView;

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
        mBackgroundLight = true;
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
        // Map the real exam number with its index in the listView
        mExamIdxToNumber = new HashMap<>();
        int examIdx = 0;
        for (Exam exam : exams) {
            mListView.getItems().add("Examen #" +
                                             exam.getExamNumber() +
                                             " - Grupo: " + cbGrupoG.getValue().toString());
            mExamIdxToNumber.put(examIdx, exam.getExamNumber());
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
                selExam(mExamIdxToNumber.get(mListView.getSelectionModel().getSelectedIndex()));
            }
        }
    };

    boolean fistTime = true;
    public void gradeTabSelected(Event event) throws IOException {
        if(mExamBank.getGroups().isEmpty()){
            cbTemaG.setDisable(true);
            cbGrupoG.setDisable(true);
            cbQuestionG.setDisable(true);
            cbAnswerG.setDisable(true);
        }
        else if(fistTime){
            fistTime = false;
            cbTemaG.setDisable(false);
            cbGrupoG.setDisable(false);
            //cbQuestionG.setDisable(false);
            //cbAnswerG.setDisable(false);
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

    public void selExam(int examNumber) {
        String subject = cbTemaG.getValue().toString();
        String group = cbGrupoG.getValue().toString();
        ArrayList<Exam> exams = mExamBank.getExams(subject, group);
        Exam exam = exams.get(examNumber);
        populateQuestionsList(exam.getQuestions());
        //cbAnswerG.getItems().clear();
        tfCalif.setText("");
        /*
        for(Question q : exam.getQuestions()) {
            if (!cbQuestionG.getItems().contains(q.getQuestion()))
                cbQuestionG.getItems().add(q.getQuestion());
        }
        arrPond = new int[exam.getQuestions().size()];
        for(int i = 0; i < arrPond.length; i++)
            arrPond[i] = 0;
        */
    }

    private void populateQuestionsList(ArrayList<Question> questions) {
        int questionIndex = 1;
        VBox questionsBox = new VBox();
        for (Question question : questions) {
            AnchorPane questionPane = new AnchorPane();
            questionPane.setPadding(new Insets(20, 20, 20, 20));
            questionPane.setStyle("-fx-background-color:" + getBackgroundColor());
            Label questionLabel = new Label("Pregunta #" + Integer.toString(questionIndex) + ":");
            questionLabel.setStyle("-fx-font-weight: bold;");
            questionLabel.setLayoutX(14.0);
            questionLabel.setLayoutY(14.0);
            questionLabel.setId("lblQuestion" + Integer.toString(questionIndex));
            Label answerLabel = new Label("Opción elegida:");
            answerLabel.setLayoutX(14.0);
            answerLabel.setLayoutY(48.0);
            JFXComboBox answerCb = new JFXComboBox();
            for (Answer answer : question.getAnswers()) {
                answerCb.getItems().add(answer.getAnswer());
            }
            answerCb.setLayoutX(14);
            answerCb.setLayoutY(64);
            answerCb.setId("cbAnswer" + Integer.toString(questionIndex));
            Label scoreLabel = new Label("");
            scoreLabel.setLayoutX(150.0);
            scoreLabel.setLayoutY(14.0);
            scoreLabel.setId("lblScore" + Integer.toString(questionIndex));
            questionPane.getChildren().addAll(questionLabel, answerLabel, answerCb, scoreLabel);
            questionsBox.getChildren().add(questionPane);
            questionIndex++;
        }
        viewQuestionsContainer.setContent(questionsBox);
    }

    private String getBackgroundColor() {
        if (mBackgroundLight) {
            mBackgroundLight = false;
            return "#fafafa";
        }
        mBackgroundLight = true;
        return "#e1e1e1";
    }

    public void selQuestion(ActionEvent actionEvent){
        cbAnswerG.getItems().clear();
        String subject = cbTemaG.getValue().toString();
        String question = cbQuestionG.getValue().toString();
        String group = cbGrupoG.getValue().toString();
        Exam exam = mExamBank.getExam(subject,
                                      group,
                                      mExamIdxToNumber.get(mListView.getSelectionModel()
                                                                    .getSelectedIndex()));
        for (Question q : exam.getQuestions()) {
            if (q.getQuestion().equals(cbQuestionG.getValue().toString())) {
                for(Answer a : q.getAnswers()) {
                    if(!cbAnswerG.getItems().contains(a.getAnswer())) {
                        cbAnswerG.getItems().add(a.getAnswer());
                    }
                }
            }
        }
    }

    public void selAnswer(ActionEvent actionEvent) {
        String subject = cbTemaG.getValue().toString();
        String group = cbGrupoG.getValue().toString();
        Exam exam = mExamBank.getExam(subject,
                                      group,
                                      mExamIdxToNumber.get(mListView.getSelectionModel()
                                                                    .getSelectedIndex()));
        int iC = 0;
        for (Question q : exam.getQuestions()) {
            if(q.getQuestion().equals(cbQuestionG.getValue().toString())){
                for(Answer a : q.getAnswers()){
                    if(cbAnswerG.getValue() != null && a.getAnswer().equals(cbAnswerG.getValue().toString())) {
                        arrPond[iC] = a.getWeight();
                        tfPond.setText(a.getWeight() + "");
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
