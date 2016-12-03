package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * ViewExamsController
 *
 * This class is the view controller for the View Generated Exams interface.
 */
public class ViewExamsController {
    private JFXListView mExamListView;
    private ChangeListener mSubjectListener;
    private ChangeListener mGroupListener;
    private ChangeListener mExamListListener;
    private boolean mSubjectListenerActive;
    private boolean mGroupListenerActive;
    private HashMap<Integer, Integer> mExamIdxToNumber;

    @FXML private ComboBox cbSubjectViewExams;
    @FXML private ComboBox cbGroupViewExams;
    @FXML private AnchorPane parentContainer;
    @FXML private JFXButton buttonDeleteExam;
    @FXML private JFXButton buttonDownloadLatex;
    @FXML private JFXButton buttonDownloadPdf;
    private ExamBank mExamBank;
    // Keep a reference to the Generate tab to be able to call its generate methods
    private GenerateExamsController mGenerateExamsController;

    public ViewExamsController() {
        mSubjectListenerActive = false;
        mGroupListenerActive = false;
    }

    public void initialize() {
        // Create the change listeners for the ComboBoxes and the ListView
        setListeners();
    }

    public void setModel(ExamBank examBank, GenerateExamsController generateExamsController) {
        mExamBank = examBank;
        mGenerateExamsController = generateExamsController;
    }

    /**
     * setListeners
     *
     * Set the listeners responsible for handling the actions of the cbSubjectViewExams and
     * cbGroupViewExams comboboxes and the mExamListView listview.
     *
     */
    private void setListeners() {
        // Called when the user has selected a subject in the cbSubjectViewExams combobox
        mSubjectListener = (ov, t, t1) -> {
            if (mSubjectListenerActive) {
                // The grade field
                resetViewFields(true, false);
            }
        };

        // Called when the user has selected a group in the cbSubjectViewExams cbGroupViewExams
        mGroupListener = (ov, t, t1) -> {
            if (mGroupListenerActive) {
                resetViewFields(true, true);
            }
        };

        // Called when an exam has been selected from the ListView
        // Enable the delete and download buttons
        mExamListListener = (ov, t, t1) -> enableAllButtons();
    }

    /**
     * resetViewFields
     *
     * Dump the ExamBank into the ViewExams interface. If the user has not selected any subject
     * and group, select the first one. Also refresh the contents of the displayed ListView
     * @param subjectWasSelected If true, then don't reset the subject combobox
     * @param groupWasSelected If true, then don't reset the group combobox
     *
     */
    private void resetViewFields(Boolean subjectWasSelected, Boolean groupWasSelected) {
        if (!subjectWasSelected) {
            // Disable the listener on the combobox to avoid a callback loop when selecting the first subject
            mSubjectListenerActive = false;
            // Fill the subject combobox and select the first one
            cbSubjectViewExams.getItems().clear();
            for (HashMap.Entry<String, ArrayList<Group>> subject : mExamBank.getGroups().entrySet()) {
                cbSubjectViewExams.getItems().add(subject.getKey());
            }
            cbSubjectViewExams.getSelectionModel().selectFirst();

            // After reseting the subject, the group needs to be reset as well
            groupWasSelected = false;
        }

        if (!groupWasSelected) {
            mGroupListenerActive = false;
            // Fill the group combobox with the selected subject groups and select the first one
            cbGroupViewExams.getItems().clear();
            for (Group group : mExamBank.getGroups(cbSubjectViewExams.getValue().toString())) {
                cbGroupViewExams.getItems().add(group.getGroupName());
            }
            cbGroupViewExams.getSelectionModel().selectFirst();
        }

        // Refresh the contents of the displayed ListView
        refreshListView();
        mSubjectListenerActive = true;
        mGroupListenerActive = true;
    }

    /**
     * refreshListView
     *
     * Delete any existing ListView and create a new one with the exams from the selected subject
     * and group.
     *
     */
    private void refreshListView() {
        // Disable every button as no exam is currently selected
        disableAllButtons();

        if (mExamListView != null) {
            parentContainer.getChildren().remove(mExamListView);
        }
        mExamListView = new JFXListView<String>();
        ArrayList<Exam> exams = mExamBank
                                                 .getExams(cbSubjectViewExams.getValue().toString(),
                                                           cbGroupViewExams.getValue().toString());
        // Map the real exam number with its index in the listView
        mExamIdxToNumber = new HashMap<>();
        int examIdx = 0;
        for (Exam exam : exams) {
            mExamListView.getItems().add("Examen #" +
                                             exam.getExamNumber() +
                                             " - Grupo: " + cbGroupViewExams.getValue().toString());
            mExamIdxToNumber.put(examIdx, exam.getExamNumber());
            examIdx++;
        }
        mExamListView.getSelectionModel().selectedItemProperty().addListener(mExamListListener);
        parentContainer.getChildren().add(mExamListView);
        AnchorPane.setLeftAnchor(mExamListView, 0.0);
        AnchorPane.setRightAnchor(mExamListView, 0.0);
        AnchorPane.setTopAnchor(mExamListView, 0.0);
        AnchorPane.setBottomAnchor(mExamListView, 0.0);
    }

    /**
     * enableAllButtons
     *
     * Set all the buttons in the view to disabled
     *
     */
    private void disableAllButtons() {
        buttonDeleteExam.setDisable(true);
        buttonDownloadLatex.setDisable(true);
        buttonDownloadPdf.setDisable(true);
    }

    /**
     * enableAllButtons
     *
     * Set all the buttons in the view to enabled
     *
     */
    private void enableAllButtons() {
        buttonDeleteExam.setDisable(false);
        buttonDownloadLatex.setDisable(false);
        buttonDownloadPdf.setDisable(false);
    }

    /**
     * loadViewExamsForm
     *
     * Initialize view exam fields.
     *
     */
    public void loadViewExamsForm() {
        if (mExamBank.getGroups().isEmpty()) {
            cbSubjectViewExams.setDisable(true);
            cbGroupViewExams.setDisable(true);
            disableAllButtons();
        }
        else {
            cbSubjectViewExams.setDisable(false);
            cbGroupViewExams.setDisable(false);
            disableAllButtons();
            resetViewFields(false, false);
            cbSubjectViewExams.getSelectionModel().selectedItemProperty().addListener(mSubjectListener);
            cbGroupViewExams.getSelectionModel().selectedItemProperty().addListener(mGroupListener);
        }
    }

    /**
     * downLatexSelectedExam
     *
     * The user clicked the "delete exam" button. Confirm the user action with a Confirmation
     * alert and delete the exam form the ExamBank.
     * @param actionEvent The context in which the user click the delete exam button.
     */
    public void deleteSelectedExam(ActionEvent actionEvent) {
        ButtonType btnSi = new ButtonType("Si");
        ButtonType btnNo = new ButtonType("No");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación de examen");
        alert.setContentText("¿Está seguro de querer eliminar el examen " +
                                    mExamListView.getSelectionModel()
                                                 .getSelectedItem()
                                                 .toString() +
                                    " de la lista de exámenes " +
                                    "generados en el programa?");
        alert.getButtonTypes().setAll(btnNo, btnSi);
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btnSi) {
            String subject = cbSubjectViewExams.getValue().toString();
            String group = cbGroupViewExams.getValue().toString();
            int examNumber = mExamIdxToNumber.get(mExamListView.getSelectionModel().getSelectedIndex());
            mExamBank.deleteExam(subject, group, examNumber);
            // Refresh the entire view to account for the deletion of a group or subject after
            // deleting the exam
            loadViewExamsForm();
        }
    }

    /**
     * downLatexSelectedExam
     *
     * The user clicked the "download exam in LaTeX" button. Save that exam to a latex file.
     * TODO: This method is very similar to downPdfSelectedExam and can be further abstracted
     * @param actionEvent The context in which the user click the download exam button.
     */
    public void downLatexSelectedExam(ActionEvent actionEvent) {
        String subject = cbSubjectViewExams.getValue().toString();
        String group = cbGroupViewExams.getValue().toString();
        int examNumber = mExamIdxToNumber.get(mExamListView.getSelectionModel().getSelectedIndex());
        Exam exam = mExamBank
                .getExam(subject, group, examNumber);

        // Encapsulate the exam in an ArrayList because that's how the method wants it
        String latexExam = ExamTemplate.makeLatexExams(new ArrayList<Exam>() {{ add(exam); }});

        final String filename = "Examen #" + Integer.toString(examNumber) + " - " + group;
        // Download the exam as a latex file
        mGenerateExamsController.downloadLatexExams(actionEvent, filename, latexExam);
    }

    /**
     * downPdfSelectedExam
     *
     * The user clicked the "download exam in Pdf" button. Save that exam to a PDF file.
     * @param actionEvent The context in which the user click the download exam button.
     */
    public void downPdfSelectedExam(ActionEvent actionEvent) {
        String subject = cbSubjectViewExams.getValue().toString();
        String group = cbGroupViewExams.getValue().toString();
        int examNumber = mExamIdxToNumber.get(mExamListView.getSelectionModel().getSelectedIndex());
        Exam exam = mExamBank.getExam(subject, group, examNumber);

        // Encapsulate the exam in an ArrayList because that's how the method wants it
        String latexExam = ExamTemplate.makeLatexExams(new ArrayList<Exam>() {{ add(exam); }});

        final String filename = "Examen #" + Integer.toString(examNumber) + " - " + group;
        // Download the exam as a PDF file
        mGenerateExamsController.downloadPdfExams(actionEvent, filename, latexExam);
    }
}
