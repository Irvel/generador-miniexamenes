package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Answer;
import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.Group;
import GeneradorMiniexamenes.model.Question;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * GradeExamsController
 *
 * This class is the view controller for the ImportExport exams interface. It is responsible for
 * inflating the fxml into an object and adding it to the displayed layout. It is also the one
 * that handles actions inside the view.
 */
public class GradeExamsController {
    private MainController mParentController;

    private HashMap<Integer, Integer> mExamIdxToNumber;
    private HashMap<Question, Integer> mQuestionToNumber;
    private HashMap<Integer, Integer> mQuestionNumToValue;
    private boolean mSubjectListenerActive;
    private boolean mGroupListenerActive;
    private boolean mBackgroundLight;
    private boolean mFirstGradeViewLoad;

    @FXML private JFXComboBox comboBoxSubjectGrade;
    @FXML private JFXComboBox comboBoxGroupGrade;
    @FXML private Label labelGrade;
    @FXML private AnchorPane viewGradeExamsContainer;
    @FXML private ScrollPane viewQuestionsContainer;
    private JFXListView mListView;

    // Change Listeners for interacting with the grade form
    private ChangeListener mSubjectListener;
    private ChangeListener mGroupListener;
    private ChangeListener mExamListListener;

    public GradeExamsController() {
        mSubjectListenerActive = false;
        mGroupListenerActive = false;
        // Alternate between the shade of gray used for the background in the exam questions
        mBackgroundLight = true;
        setListeners();
        // This makes the app load the listeners for a theme and group change
        mFirstGradeViewLoad = true;
    }

    public void injectMainController(MainController mainController) {
        // Keep a reference to the main controller to get important shared variables
        mParentController = mainController;
    }

    /**
     * resetGradeFields
     *
     * Basically dump the ExamBank into the GradeView. Specifically, it resets the grade exams form
     * fields with the available subjects, groups and exams from the ExamBank.
     *
     */
    private void resetGradeFields(Boolean subjectWasSelected, Boolean groupWasSelected) {
        // Reset the questions view
        viewQuestionsContainer.setContent(null);
        labelGrade.setText("Calificación:");
        if (!subjectWasSelected) {
            // Disable the listener on the combobox to avoid a callback loop when selecting the first subject
            mSubjectListenerActive = false;
            // Fill the subject combobox and select the first one
            comboBoxSubjectGrade.getItems().clear();
            for (HashMap.Entry<String, ArrayList<Group>> subject : mParentController.getExamBank().getGroups().entrySet()) {
                comboBoxSubjectGrade.getItems().add(subject.getKey());
            }
            comboBoxSubjectGrade.getSelectionModel().selectFirst();

            // After reseting the subject, the group needs to be reset as well
            groupWasSelected = false;
        }

        if (!groupWasSelected) {
            mGroupListenerActive = false;
            // Fill the group combobox with the selected subject groups and select the first one
            comboBoxGroupGrade.getItems().clear();
            for (Group group : mParentController.getExamBank().getGroups(comboBoxSubjectGrade.getValue().toString())) {
                comboBoxGroupGrade.getItems().add(group.getGroupName());
            }
            comboBoxGroupGrade.getSelectionModel().selectFirst();
        }
        if (mListView != null) {
            viewGradeExamsContainer.getChildren().remove(mListView);
        }
        mListView = new JFXListView<String>();
        ArrayList<Exam> exams = mParentController.getExamBank().getExams(comboBoxSubjectGrade.getValue().toString(),
                                                                         comboBoxGroupGrade.getValue().toString());
        // Map the real exam number with its index in the listView
        mExamIdxToNumber = new HashMap<>();
        int examIdx = 0;
        for (Exam exam : exams) {
            mListView.getItems().add("Examen #" +
                                             exam.getExamNumber() +
                                             " - Grupo: " + comboBoxGroupGrade.getValue().toString());
            mExamIdxToNumber.put(examIdx, exam.getExamNumber());
            examIdx++;
        }
        mListView.getSelectionModel().selectedItemProperty().addListener(mExamListListener);
        mListView.setPrefWidth(150.0);
        viewGradeExamsContainer.getChildren().add(mListView);
        AnchorPane.setLeftAnchor(mListView, 0.0);
        AnchorPane.setRightAnchor(mListView, 0.0);
        AnchorPane.setTopAnchor(mListView, 0.0);
        AnchorPane.setBottomAnchor(mListView, 0.0);
        mSubjectListenerActive = true;
        mGroupListenerActive = true;
    }

    /**
     * setListeners
     *
     * Set the listeners responsible for handling the actions of the grade from view.
     *
     */
    private void setListeners() {
        mSubjectListener = (ov, t, t1) -> {
            if (mSubjectListenerActive) {
                resetGradeFields(true, false);
            }
        };
        mGroupListener = (ov, t, t1) -> {
            if (mGroupListenerActive) {
                resetGradeFields(true, true);
            }
        };
        // Called when an exam has been selected from the ListView
        mExamListListener = (ov, t, t1) -> {
            if (mGroupListenerActive) {
                examSelected(mExamIdxToNumber.get(mListView.getSelectionModel().getSelectedIndex()));
            }
        };
    }

    public void examSelected(int examNumber) {
        String subject = comboBoxSubjectGrade.getValue().toString();
        String group = comboBoxGroupGrade.getValue().toString();
        Exam exam = mParentController.getExamBank().getExam(subject, group, examNumber);
        populateQuestionsList(exam.getQuestions());
        labelGrade.setText("Calificación:");
    }

    /**
     * populateQuestionsList
     *
     * Fill the right pane in the view with the questions from the selected exam. Each question
     * has the option to select an answer and when doing so, the score so far is auto calculated
     * and the obtained percentage of points is given for that answer.
     *
     */
    private void populateQuestionsList(ArrayList<Question> questions) {
        int questionIndex = 1;
        VBox questionsBox = new VBox();
        mQuestionToNumber = new HashMap<>();
        mQuestionNumToValue = new HashMap<>();
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (Question question : questions) {
            mQuestionToNumber.put(question, questionIndex);
            AnchorPane questionPane = new AnchorPane();
            questionPane.setPadding(new Insets(20, 0, 10, 20));
            questionPane.setStyle("-fx-background-color:" + getBackgroundColor());
            Label questionLabel = new Label("Pregunta #" + Integer.toString(questionIndex) + ":");
            questionLabel.setStyle("-fx-font-weight: bold");
            questionLabel.setLayoutX(14.0);
            questionLabel.setLayoutY(14.0);
            questionLabel.setId("lblQuestion" + Integer.toString(questionIndex));
            Label answerLabel = new Label("Opción elegida:");
            answerLabel.setLayoutX(14.0);
            answerLabel.setLayoutY(42.0);
            JFXComboBox answerCb = new JFXComboBox();
            int letterIdx = 0;
            for (Answer answer : question.getAnswers()) {
                answerCb.getItems().add("(" + alphabet[letterIdx] + ") " + answer.getAnswer());
                letterIdx++;
            }
            answerCb.setLayoutX(14);
            answerCb.setLayoutY(64);
            answerCb.setUserData(question);
            answerCb.setOnAction((event) -> {
                String selectedAnswer = answerCb.getSelectionModel().getSelectedItem().toString();
                selAnswer(answerCb.getScene(), selectedAnswer, (Question) answerCb.getUserData());
            });
            Label scoreLabel = new Label("");
            scoreLabel.setLayoutX(168.0);
            scoreLabel.setLayoutY(50.0);
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

    public void selAnswer(Scene scene, String selectedAnwer, Question question) {
        String subject = comboBoxSubjectGrade.getValue().toString();
        String group = comboBoxGroupGrade.getValue().toString();
        Exam exam = mParentController.getExamBank().getExam(subject,
                                      group,
                                      mExamIdxToNumber.get(mListView.getSelectionModel()
                                                                    .getSelectedIndex()));
        int questionNumber = mQuestionToNumber.get(question);
        Label scoreLabel = (Label) scene.lookup("#lblScore" + Integer.toString(questionNumber));
        for (Answer answer : question.getAnswers()) {
            // The substring is to account for the first 4 characters being the question Letter
            if (answer.getAnswer().equalsIgnoreCase(selectedAnwer.substring(4))) {
                scoreLabel.setText("Porcentaje obtenido: " + Integer.toString(answer.getWeight()));
                mQuestionNumToValue.put(questionNumber, answer.getWeight());
            }
        }
        updateTotalScore(scene, exam);
    }

    private void updateTotalScore(Scene scene, Exam exam) {
        int scoreSum = 0;
        for (int questionNumber : mQuestionNumToValue.keySet()) {
            scoreSum += mQuestionNumToValue.get(questionNumber);
        }
        labelGrade.setText("Calificación del examen #" +
                                Integer.toString(exam.getExamNumber()) + ":   " +
                                String.format("%.2f",(scoreSum * 1.0 / exam.getQuestions()
                                                                           .size())));
    }

    public void tabSelected() {
        if(mParentController.getExamBank().getGroups().isEmpty()) {
            comboBoxSubjectGrade.setDisable(true);
            comboBoxGroupGrade.setDisable(true);
        }
        else if(mFirstGradeViewLoad) {
            mFirstGradeViewLoad = false;
            setListeners();
            comboBoxSubjectGrade.setDisable(false);
            comboBoxGroupGrade.setDisable(false);
            resetGradeFields(false, false);
            comboBoxSubjectGrade.getSelectionModel().selectedItemProperty().addListener(mSubjectListener);
            comboBoxGroupGrade.getSelectionModel().selectedItemProperty().addListener(mGroupListener);
        }
        else {
            resetGradeFields(false, false);
            viewQuestionsContainer.setContent(null);
            labelGrade.setText("Calificación:");
        }
    }
}
