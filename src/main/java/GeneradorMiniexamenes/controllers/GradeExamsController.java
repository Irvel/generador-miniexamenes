package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

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
    // A reference to the Model of the application
    private ExamBank mExamBank;

    private HashMap<Question, Integer> mQuestionToNumber;
    private HashMap<Integer, Integer> mQuestionNumToValue;
    private ObservableList<ExamMainGrade> mQuestionsData;
    private boolean mSubjectListenerActive;
    private boolean mGroupListenerActive;
    private boolean mBackgroundLight;

    @FXML private JFXComboBox comboBoxSubjectGrade;
    @FXML private JFXComboBox comboBoxGroupGrade;
    @FXML private Label labelGrade;
    @FXML private AnchorPane viewGradeExamsContainer;
    @FXML private ScrollPane viewQuestionsContainer;
    @FXML private TableView<ExamMainGrade> tableMainExam;
    @FXML private TableColumn<ExamMainGrade, Number> questionNumberColumn;
    @FXML private TableColumn<ExamMainGrade, String> answerColumn;
    @FXML private TableColumn<ExamMainGrade, String> weightColumn;
    @FXML private TableColumn<ExamMainGrade, Number> questionValueColumn;
    private JFXListView<Exam> mExamListView;

    // Change Listeners for interacting with the grade form
    private ChangeListener mSubjectListener;
    private ChangeListener mGroupListener;
    private ChangeListener mExamListListener;

    public GradeExamsController() {
        mExamListView = new JFXListView<>();
        mQuestionsData = FXCollections.observableArrayList();
        mSubjectListenerActive = false;
        mGroupListenerActive = false;
        // Alternate between the shade of gray used for the background in the exam questions
        mBackgroundLight = true;
    }

    @FXML
    public void initialize(){
        setListeners();
        comboBoxSubjectGrade.getSelectionModel().selectedItemProperty().addListener(mSubjectListener);
        comboBoxGroupGrade.getSelectionModel().selectedItemProperty().addListener(mGroupListener);

        // Exam ListView setup
        // Only show the exam number instead of a more detailed description
        mExamListView.setCellFactory(new Callback<ListView<Exam>, ListCell<Exam>>() {
            @Override
            public ListCell<Exam> call(ListView<Exam> param) {
                return new ListCell<Exam>(){
                    @Override
                    public void updateItem(Exam exam, boolean bln) {
                        super.updateItem(exam, bln);
                        if (exam != null) {
                            setText(exam.getExamNumber() +  "");
                        }
                        else {
                            setText("");
                        }
                    }

                };
            }
        });
        mExamListView.setPrefWidth(20.0);
        viewGradeExamsContainer.getChildren().add(mExamListView);
        mExamListView.getSelectionModel().selectedItemProperty().addListener(mExamListListener);
        AnchorPane.setLeftAnchor(mExamListView, 0.0);
        AnchorPane.setRightAnchor(mExamListView, 0.0);
        AnchorPane.setTopAnchor(mExamListView, 0.0);
        AnchorPane.setBottomAnchor(mExamListView, 0.0);

        // Setup the grading table
        questionNumberColumn.setCellValueFactory(cellData -> cellData.getValue().questionNumberProperty());
        answerColumn.setCellValueFactory(cellData -> cellData.getValue().answerLetterProperty());
        weightColumn.setCellValueFactory(cellData -> cellData.getValue().answerWeightProperty());
        questionValueColumn.setCellValueFactory(cellData -> cellData.getValue()
                                                                    .questionValueProperty());

        answerColumn.setCellFactory(column -> EditCell.createStringEditCell());
        answerColumn.setEditable(true);
        questionValueColumn.setCellFactory(column -> EditCell.createNumberEditCell());

        // switch to edit mode on keypress
        // this must be KeyEvent.KEY_PRESSED so that the key gets forwarded to the editing cell; it wouldn't be forwarded on KEY_RELEASED
        tableMainExam.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if ( event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                tableMainExam.requestFocus();
                return;
            }

            // switch to edit mode on keypress, but only if we aren't already in edit mode
            if( tableMainExam.getEditingCell() == null) {
                if( event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
                    TablePosition focusedCellPosition = tableMainExam.getFocusModel().getFocusedCell();
                    tableMainExam.edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
                }
            }

        });

        tableMainExam.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if( event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                // Clear the current selection
                TablePosition pos = tableMainExam.getFocusModel().getFocusedCell();

                if (pos.getRow() == -1) {
                    tableMainExam.getSelectionModel().select(0);
                }
                // select next row, but same column as the current selection
                else if (pos.getRow() < tableMainExam.getItems().size() -1) {
                    tableMainExam.getSelectionModel().clearAndSelect( pos.getRow() + 1, pos.getTableColumn());
                    tableMainExam.requestFocus();
                }
            }
        });
        // single cell selection mode
        tableMainExam.getSelectionModel().setCellSelectionEnabled(true);
        tableMainExam.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableMainExam.setEditable(true);
        answerColumn.setOnEditCommit(event -> {
            // This is the index on the ObservableList, so it starts from 0
            int questionIdx = event.getRowValue().getQuestionNumber() - 1;
            String enteredValue = event.getNewValue().trim();
            mQuestionsData.get(questionIdx).setAnswerLetter(enteredValue);
            if (!isValidLetter(enteredValue)) {
                //event.getRowValue().setAnswerLetter("");
                //event.getRowValue().setAnswerWeight("");
                mQuestionsData.get(questionIdx).setAnswerWeight(null);
                mQuestionsData.get(questionIdx).setAnswerLetter(null);
                return;
            }
            // When an answer has been entered, try to get the value of that answer
            char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            Question question = event.getRowValue().getQuestion();
            int letterIdx = 0;
            int targetWeight = 0;
            for (Answer answer : question.getAnswers()) {
                if (String.valueOf(alphabet[letterIdx]).equalsIgnoreCase(enteredValue)) {
                    targetWeight = answer.getWeight();
                    break;
                }
                letterIdx++;
            }
            if (letterIdx == question.getAnswers().size()) {
                mQuestionsData.get(questionIdx).setAnswerLetter(null);
                mQuestionsData.get(questionIdx).setAnswerWeight(null);
                //event.getRowValue().setAnswerLetter("");
            }
            else {
                mQuestionsData.get(questionIdx).setAnswerWeight(targetWeight +"");
                //event.getRowValue().setAnswerWeight(targetWeight + "");
            }
        });

        /*questionNumberColumn.setCellFactory(new Callback<TableColumn<ExamMainGrade,
                ExamMainGrade>, TableCell<ExamMainGrade, ExamMainGrade>>() {
            @Override
            public TableCell<ExamMainGrade, ExamMainGrade> call(TableColumn<ExamMainGrade, ExamMainGrade> param) {
                return new TableCell<ExamMainGrade, ExamMainGrade>() {
                    @Override protected void updateItem(ExamMainGrade item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null) {
                            int index = this.getTableRow().getIndex();
                            if(index < tableMainExam.getItems().size()) {
                                int rowNum = index + 1;
                                setText( String.valueOf(rowNum));
                            } else {
                                setText("");
                            }

                        } else {
                            setText("");
                        }

                    }
                };
            }
        });*/

    }

    private boolean isValidLetter(String text) {
        // Ensure that the user entered a single letter only
        return text != null && text.length() == 1 && Character.isLetter(text.charAt(0));
    }

    public void setModel(ExamBank examBank) {
        mExamBank = examBank;
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
            for (HashMap.Entry<String, ArrayList<Group>> subject : mExamBank.getGroups().entrySet()) {
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
            for (Group group : mExamBank.getGroups(comboBoxSubjectGrade.getValue().toString())) {
                comboBoxGroupGrade.getItems().add(group.getGroupName());
            }
            comboBoxGroupGrade.getSelectionModel().selectFirst();
        }

        // Update the list of exams view
        mExamListView.getItems().clear();
        ArrayList<Exam> exams = mExamBank.getExams(comboBoxSubjectGrade.getValue().toString(),
                                                                         comboBoxGroupGrade.getValue().toString());
        mExamListView.getItems().addAll(exams);
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
        // An Subject from the subject combobox has been selected
        mSubjectListener = (ov, t, t1) -> {
            if (mSubjectListenerActive) {
                resetGradeFields(true, false);
            }
        };
        // An group from the group combobox has been selected
        mGroupListener = (ov, t, t1) -> {
            if (mGroupListenerActive) {
                resetGradeFields(true, true);
            }
        };
        // Called when an exam has been selected from the ListView
        mExamListListener = (ov, t, t1) -> {
            examSelected(mExamListView.getSelectionModel().getSelectedItem());
        };
    }

    public void examSelected(Exam exam) {
        populateMainTable(exam.getQuestions());
        labelGrade.setText("");
    }

    private void populateMainTable(ArrayList<Question> questions) {
        mQuestionsData.clear();
        int questionNumber = 1;
        for (Question question : questions) {
            mQuestionsData.add(new ExamMainGrade(questionNumber, "", 0, 100, question));
            questionNumber++;
        }
        tableMainExam.setItems(mQuestionsData);
        tableMainExam.getSelectionModel().selectFirst();
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
        Exam exam = mExamListView.getSelectionModel().getSelectedItem();
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

    public void tabSelected() {
        if(mExamBank.getGroups().isEmpty()) {
            comboBoxSubjectGrade.setDisable(true);
            comboBoxGroupGrade.setDisable(true);
        }
        else {
            resetGradeFields(false, false);
            viewQuestionsContainer.setContent(null);
        }
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

    public void cleanTableAction(ActionEvent actionEvent) {
        if (mExamListView == null || mExamListView.getSelectionModel().getSelectedItem() == null) {
            mExamListView.getSelectionModel().selectFirst();
        }
        examSelected(mExamListView.getSelectionModel().getSelectedItem());
    }

    public void gradeTableAction(ActionEvent actionEvent) {
        int scoreSum = 0;
        int questionsTotal = 0;
        boolean allRowsAreValid = true;
        for (ExamMainGrade tableItem : mQuestionsData) {
            if (rowIsValid(tableItem)){
                scoreSum += (Integer.parseUnsignedInt(tableItem.getAnswerWeight())) * tableItem.getQuestionValue();
                questionsTotal += tableItem.getQuestionValue();
            }
            else {
                allRowsAreValid = false;
            }
        }
        if (allRowsAreValid) {
            scoreSum /= questionsTotal;
            labelGrade.setText("Calificación: " + scoreSum);
        }
    }

    // Checks that all the fields in a row from the table are valid
    private boolean rowIsValid(ExamMainGrade tableItem) {
        // Check that nothing is null
        if (tableItem == null || tableItem.getAnswerLetter() == null ||
                tableItem.getAnswerWeight() == null || tableItem.getQuestionNumber() < 0 ||
                tableItem.getQuestionValue() < 0) {
            return false;
        }

        // Check that the answer weight is a positive integer
        try {
            Integer.parseUnsignedInt(tableItem.getAnswerWeight());
        }
        catch(NumberFormatException e){
            return false;
        }

        // Check that the answer letter is a valid letter
        if (!isValidLetter(tableItem.getAnswerLetter())) {
            return false;
        }
        // Check that the answer letter exists in the answer options
        final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray(); // Convert idx to letter
        boolean exists = false;
        for (int i = 0; i < tableItem.getQuestion().getAnswers().size(); i++) {
            if (String.valueOf(alphabet[i]).equalsIgnoreCase(tableItem.getAnswerLetter())) {
                exists = true;
            }
        }
        return exists;
    }
}
