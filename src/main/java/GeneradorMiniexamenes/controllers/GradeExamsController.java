package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
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

    private ObservableList<ExamMainGrade> mQuestionsData;
    private ObservableList<ExamAnswers> mAnswersData;
    private boolean mSubjectListenerActive;
    private boolean mGroupListenerActive;
    private boolean mTableListenerActive;

    @FXML private JFXComboBox comboBoxSubjectGrade;
    @FXML private JFXComboBox comboBoxGroupGrade;
    @FXML private JFXButton buttonLimpiar;
    @FXML private JFXButton buttonCalificar;
    @FXML private Label labelGrade;
    @FXML private Label answersLabel;
    @FXML private AnchorPane viewGradeExamsContainer;
    @FXML private TableView<ExamMainGrade> tableMainExam;
    @FXML private TableView<ExamAnswers> tableAnswers;
    @FXML private TableColumn<ExamMainGrade, Number> questionNumberColumn;
    @FXML private TableColumn<ExamMainGrade, String> answerColumn;
    @FXML private TableColumn<ExamMainGrade, String> weightColumn;
    @FXML private TableColumn<ExamMainGrade, Number> questionValueColumn;
    @FXML private TableColumn<ExamAnswers, String> answerLetterColumn;
    @FXML private TableColumn<ExamAnswers, String> answerWeightColumn;
    private JFXListView<Exam> mExamListView;

    private final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    // Change Listeners for interacting with the grade form
    private ChangeListener mSubjectListener;
    private ChangeListener mGroupListener;
    private ChangeListener mExamListListener;

    public GradeExamsController() {
        mExamListView = new JFXListView<>();
        mQuestionsData = FXCollections.observableArrayList();
        mAnswersData = FXCollections.observableArrayList();
        mSubjectListenerActive = false;
        mGroupListenerActive = false;
        mTableListenerActive = false;
    }

    @FXML
    public void initialize(){
        setListeners();
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
        Label placeholder = new Label("Seleccionar un examen de la izquierda para comenzar a calificar.");
        placeholder.setWrapText(true);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setPadding(new Insets(0,18,0,18));
        tableMainExam.setPlaceholder(placeholder);
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
                mQuestionsData.get(questionIdx).setAnswerWeight(null);
                mQuestionsData.get(questionIdx).setAnswerLetter(null);
                return;
            }
            // When an answer has been entered, try to get the value of that answer
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
            }
            else {
                mQuestionsData.get(questionIdx).setAnswerWeight(targetWeight +"");
                mQuestionsData.get(questionIdx).setAnswerLetter(enteredValue);
            }
        });

        tableAnswers.setPlaceholder(new Label(""));
        answerLetterColumn.setCellValueFactory(cellData -> cellData.getValue().answerLetterProperty());
        answerWeightColumn.setCellValueFactory(cellData -> cellData.getValue().answerWeightProperty());
        tableAnswers.getSelectionModel().setCellSelectionEnabled(false);
    }

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
            buttonLimpiar.setDisable(false);
            buttonCalificar.setDisable(false);
            examSelected(mExamListView.getSelectionModel().getSelectedItem());
        };

        comboBoxSubjectGrade.getSelectionModel().selectedItemProperty().addListener(mSubjectListener);
        comboBoxGroupGrade.getSelectionModel().selectedItemProperty().addListener(mGroupListener);

        // Selecting something from the main table

        tableMainExam.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            if (mTableListenerActive) {
                if (mAnswersData != null) {
                    mAnswersData.clear();
                }
                if (tableMainExam != null && tableMainExam.getSelectionModel() != null &&
                        tableMainExam.getSelectionModel().getSelectedItems() != null &&
                        tableMainExam.getSelectionModel().getSelectedItem() != null) {
                    tableAnswers.getSelectionModel().clearSelection();
                    tableAnswers.getItems().removeAll(tableAnswers.getSelectionModel().getSelectedItems());
                    Question question = ov.getValue().getQuestion();
                    int letterIdx = 0;
                    for (Answer answer : question.getAnswers()) {
                        mAnswersData.add(new ExamAnswers(String.valueOf(alphabet[letterIdx]),
                                                         Integer.toString(answer.getWeight())));
                        letterIdx++;
                    }
                    answersLabel.setText("Respuestas pregunta #" + ov.getValue()
                                                                     .getQuestionNumber());
                    tableAnswers.setItems(mAnswersData);
                }
            }
        });
    }

    private boolean isValidLetter(String text) {
        // Ensure that the user entered a single letter only
        return (text != null && text.length() == 1 && Character.isLetter(text.charAt(0)));
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
        if (mExamListView != null && mExamListView.getItems() != null) {
            mExamListView.getItems().clear();
        }
        ArrayList<Exam> exams = mExamBank.getExams(comboBoxSubjectGrade.getValue().toString(),
                                                                         comboBoxGroupGrade.getValue().toString());
        mExamListView.getItems().addAll(exams);

        // Clean the grading TableView
        mQuestionsData.clear();
        tableMainExam.getSelectionModel().clearSelection();
        tableMainExam.getItems().removeAll(tableMainExam.getSelectionModel().getSelectedItems());
        labelGrade.setText("");
        answersLabel.setText("Respuestas");
        if (mAnswersData != null) {
            mAnswersData.clear();
            tableAnswers.getSelectionModel().clearSelection();
            tableAnswers.getItems().removeAll(tableAnswers.getSelectionModel().getSelectedItems());
        }
        mTableListenerActive = false;
        // Any change in the comboboxes should disable grading exams
        buttonLimpiar.setDisable(true);
        buttonCalificar.setDisable(true);

        mSubjectListenerActive = true;
        mGroupListenerActive = true;
    }

    /**
     * setListeners
     *
     * Set the listeners responsible for handling the actions of the grade from view.
     *
     */

    public void examSelected(Exam exam) {
        if (exam == null) {
            return;
        }
        populateMainTable(exam.getQuestions());
        labelGrade.setText("");
        answersLabel.setText("Respuestas");
        if (mAnswersData != null) {
            mAnswersData.clear();
            tableAnswers.getSelectionModel().clearSelection();
            tableAnswers.getItems().removeAll(tableAnswers.getSelectionModel().getSelectedItems());
        }
        mTableListenerActive = true;
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

    public void tabSelected() {
        if(mExamBank.getGroups().isEmpty()) {
            comboBoxSubjectGrade.setDisable(true);
            comboBoxGroupGrade.setDisable(true);
            buttonLimpiar.setDisable(true);
            buttonCalificar.setDisable(true);
        }
        else {
            resetGradeFields(false, false);
        }
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
        if (allRowsAreValid && !mQuestionsData.isEmpty()) {
            scoreSum /= questionsTotal;
            labelGrade.setText("Calificación: " + scoreSum);
        }
        else {
            AlertMaker.displayError("Error", "Favor de ingresar todos los campos en la " +
                    "tabla antes de calificar");
        }
    }

    // Checks that all the fields in a row from the table are valid
    // This method also alerts the user because it knows what is wrong
    private boolean rowIsValid(ExamMainGrade tableItem) {
        // Check that nothing is null
        if (tableItem == null || tableItem.getAnswerLetter() == null ||
                tableItem.getAnswerWeight() == null || tableItem.getQuestionNumber() < 0 ||
                tableItem.getQuestionValue() < 0) {
            AlertMaker.displayError("Faltan datos", "Favor de ingresar todos los campos en la " +
                    "tabla antes de calificar");
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
            AlertMaker.displayError("Error", "Favor de ingresar una letra que corresponda a " +
                    "alguna de las respuestas de la pregunta número " + tableItem.getQuestionNumber());
            return false;
        }
        // Check that the answer letter exists in the answer options
        boolean exists = false;
        for (int i = 0; i < tableItem.getQuestion().getAnswers().size(); i++) {
            if (String.valueOf(alphabet[i]).equalsIgnoreCase(tableItem.getAnswerLetter())) {
                exists = true;
            }
        }
        if (!exists) {
            AlertMaker.displayError("Error", "Favor de ingresar una letra que corresponda a " +
                    "alguna de las respuestas de la pregunta número " + tableItem.getQuestionNumber());
        }
        return exists;
    }
}
