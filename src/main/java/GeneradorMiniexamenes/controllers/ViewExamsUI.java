package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.ExamTemplate;
import GeneradorMiniexamenes.model.Group;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * ViewExamsUI
 *
 * This class is the view controller for the View Generated Exams interface.
 */
public class ViewExamsUI {
    private MainController mParentController;
    private VBox mViewExamsContainer;
    private JFXListView mExamListView;
    private ChangeListener mSubjectListener;
    private ChangeListener mGroupListener;
    private ChangeListener mExamListListener;
    private boolean mFirstLoad;
    private boolean mSubjectListenerActive;
    private boolean mGroupListenerActive;
    private HashMap<Integer, Integer> mExamIdxToNumber;

    @FXML
    ComboBox cbTemaViewExams;
    @FXML
    ComboBox cbGrupoViewExams;
    @FXML
    AnchorPane viewExamsContainer;
    @FXML
    JFXButton btnDeleteExam;
    @FXML
    JFXButton btnDownloadLatex;
    @FXML
    JFXButton btnDownloadPdf;

    public ViewExamsUI(MainController parentController) {
        // Keep a reference to the parent to access central variables
        mParentController = parentController;

        // Load the interface for viewing the previously generated exams
        inflateView();

        // Create the change listeners for the comboxes and the listview
        setListeners();
        mFirstLoad = true;
        mSubjectListenerActive = false;
        mGroupListenerActive = false;
    }

    /**
     * setListeners
     *
     * Set the listeners responsible for handling the actions of the cbTemaViewExams and
     * cbGrupoViewExams comboboxes and the mExamListView listview.
     *
     */
    private void setListeners() {
        // Called when the user has selected a subject in the cbTemaViewExams combobox
        mSubjectListener = (ov, t, t1) -> {
            if (mSubjectListenerActive) {
                // The grade field
                resetViewFields(true, false);
            }
        };

        // Called when the user has selected a group in the cbTemaViewExams cbGrupoViewExams
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
            cbTemaViewExams.getItems().clear();
            for (HashMap.Entry<String, ArrayList<Group>> subject : mParentController.getExamBank()
                                                                                    .getGroups()
                                                                                    .entrySet()) {
                cbTemaViewExams.getItems().add(subject.getKey());
            }
            cbTemaViewExams.getSelectionModel().selectFirst();

            // After reseting the subject, the group needs to be reset as well
            groupWasSelected = false;
        }

        if (!groupWasSelected) {
            mGroupListenerActive = false;
            // Fill the group combobox with the selected subject groups and select the first one
            cbGrupoViewExams.getItems().clear();
            for (Group group : mParentController.getExamBank().getGroups(cbTemaViewExams.getValue().toString())) {
                cbGrupoViewExams.getItems().add(group.getGroupName());
            }
            cbGrupoViewExams.getSelectionModel().selectFirst();
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
            viewExamsContainer.getChildren().remove(mExamListView);
        }
        mExamListView = new JFXListView<String>();
        ArrayList<Exam> exams = mParentController.getExamBank()
                                                 .getExams(cbTemaViewExams.getValue().toString(),
                                                           cbGrupoViewExams.getValue().toString());
        // Map the real exam number with its index in the listView
        mExamIdxToNumber = new HashMap<>();
        int examIdx = 0;
        for (Exam exam : exams) {
            mExamListView.getItems().add("Examen #" +
                                             exam.getExamNumber() +
                                             " - Grupo: " + cbGrupoViewExams.getValue().toString());
            mExamIdxToNumber.put(examIdx, exam.getExamNumber());
            examIdx++;
        }
        mExamListView.getSelectionModel().selectedItemProperty().addListener(mExamListListener);
        viewExamsContainer.getChildren().add(mExamListView);
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
        btnDeleteExam.setDisable(true);
        btnDownloadLatex.setDisable(true);
        btnDownloadPdf.setDisable(true);
    }

    /**
     * enableAllButtons
     *
     * Set all the buttons in the view to enabled
     *
     */
    private void enableAllButtons() {
        btnDeleteExam.setDisable(false);
        btnDownloadLatex.setDisable(false);
        btnDownloadPdf.setDisable(false);
    }

    /**
     * inflateView
     *
     * Load the fxml interface definition into an object stored in mViewExamsContainer.
     *
     */
    private void inflateView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ViewExams.fxml"));
        loader.setController(this);
        try {
            mViewExamsContainer = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadViewExamsForm
     *
     * Load the viewExam interface into the currently displayed user interface if its the first
     * time and initialize the shown fields.
     *
     */
    public void loadViewExamsForm(AnchorPane mainViewExamsContainer) {
        if (mFirstLoad) {
            mFirstLoad = false;
            mainViewExamsContainer.getChildren().add(mViewExamsContainer);
        }
        if (mParentController.getExamBank().getGroups().isEmpty()) {
            cbTemaViewExams.setDisable(true);
            cbGrupoViewExams.setDisable(true);
            disableAllButtons();
        }
        else {
            cbTemaViewExams.setDisable(false);
            cbGrupoViewExams.setDisable(false);
            disableAllButtons();
            resetViewFields(false, false);
            cbTemaViewExams.getSelectionModel().selectedItemProperty().addListener(mSubjectListener);
            cbGrupoViewExams.getSelectionModel().selectedItemProperty().addListener(mGroupListener);
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
            String subject = cbTemaViewExams.getValue().toString();
            String group = cbGrupoViewExams.getValue().toString();
            int examNumber = mExamIdxToNumber.get(mExamListView.getSelectionModel().getSelectedIndex());
            mParentController.getExamBank().deleteExam(subject, group, examNumber);
            // Refresh the entire view to account for the deletion of a group or subject after
            // deleting the exam
            loadViewExamsForm(null);
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
        String subject = cbTemaViewExams.getValue().toString();
        String group = cbGrupoViewExams.getValue().toString();
        int examNumber = mExamIdxToNumber.get(mExamListView.getSelectionModel().getSelectedIndex());
        Exam exam = mParentController.getExamBank()
                                     .getExam(subject, group, examNumber);

        // Encapsulate the exam in an ArrayList because that's how the method wants it
        String latexExam = ExamTemplate.makeLatexExams(new ArrayList<Exam>() {{ add(exam); }});

        final String filename = "Examen #" + Integer.toString(examNumber) + " - " + group;
        // Download the exam as a latex file
        mParentController.getGenerateInstance()
                         .downloadLatexExams(actionEvent, filename, latexExam);
    }

    /**
     * downPdfSelectedExam
     *
     * The user clicked the "download exam in Pdf" button. Save that exam to a PDF file.
     * @param actionEvent The context in which the user click the download exam button.
     */
    public void downPdfSelectedExam(ActionEvent actionEvent) {
        String subject = cbTemaViewExams.getValue().toString();
        String group = cbGrupoViewExams.getValue().toString();
        int examNumber = mExamIdxToNumber.get(mExamListView.getSelectionModel().getSelectedIndex());
        Exam exam = mParentController.getExamBank()
                                     .getExam(subject, group, examNumber);

        // Encapsulate the exam in an ArrayList because that's how the method wants it
        String latexExam = ExamTemplate.makeLatexExams(new ArrayList<Exam>() {{ add(exam); }});

        final String filename = "Examen #" + Integer.toString(examNumber) + " - " + group;
        // Download the exam as a PDF file
        mParentController.getGenerateInstance()
                         .downloadPdfExams(actionEvent, filename, latexExam);
    }
}
