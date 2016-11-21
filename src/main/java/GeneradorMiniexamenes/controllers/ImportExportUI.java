package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import GeneradorMiniexamenes.model.Subject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * ImportExportUI
 *
 * This class is the view controller for the ImportExport exams interface. It is responsible for
 * inflating the fxml into an object and adding it to the displayed layout. It is also the one
 * that handles actions inside the view.
 */
public class ImportExportUI {
    private MainController mParentController;
    private Import mImport;
    private Export mExport;
    private boolean mFirstLoad;

    @FXML
    JFXButton btnExport;

    @FXML
    JFXComboBox<String> cbTema;

    public ImportExportUI(MainController parentController) {
        // Keep a reference to the main controller to get important shared variables
        mParentController = parentController;
        mImport = new Import();
        mExport = new Export();
        mFirstLoad = true;
    }

    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void importAction(ActionEvent actionEvent) {
        QuestionBank imported = mImport.onClick(actionEvent, mParentController.getQuestionBank());
        if (imported != null) {
            mParentController.setQuestionBank(imported);
            // Display the now an available subject to export
            resetExportOptions();
        }
    }

    /**
     * The user clicked the export button
     * @param actionEvent
     */
    public void exportAction(ActionEvent actionEvent) {
        // Get the selected subject object from the cbTema ComboBox
        Subject subject = mParentController.getQuestionBank()
                                           .getSubjectByName(cbTema.getValue());
        mExport.onClick(actionEvent, subject);
    }

    public void loadImportExport(VBox mainContainer) {
        // If this is the first time loading the view
        if (mFirstLoad) {
            mFirstLoad = false;
            // Load the interface for importing or exporting the QuestionBank
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ImportExport.fxml"));
            loader.setController(this);
            try {
                HBox importExportContainer = loader.load();
                // Show the import/export view to the user
                mainContainer.getChildren().add(importExportContainer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Update the form options from the loaded QuestionBank
        resetExportOptions();
    }

    private void resetExportOptions() {
        // Checks if there is at least one subject in the QuestionBank
        if (mParentController.getQuestionBank().getSubjects().isEmpty()) {
            btnExport.setDisable(true);
            cbTema.setDisable(true);
        }
        else {
            btnExport.setDisable(false);
            cbTema.setDisable(false);
            // Load each subject name from the QuestionBank into the combo box
            mParentController.getQuestionBank()
                             .getSubjects()
                             .stream()
                             .filter(s -> !cbTema.getItems().contains(s.getSubjectName()))
                             .forEach(s -> {
                                 cbTema.getItems().add(s.getSubjectName());
                             });

            // Set the preselected subject in the combo box as the first one
            cbTema.getSelectionModel().selectFirst();
        }
    }
}
