package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.QuestionBank;
import GeneradorMiniexamenes.model.Subject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * ImportExportController
 *
 * This class is the view controller for the ImportExport exams interface. It is responsible for
 * inflating the fxml into an object and adding it to the displayed layout. It is also the one
 * that handles actions inside the view.
 */
public class ImportExportController {
    private QuestionBank mQuestionBank;
    private Import mImport;
    private Export mExport;

    @FXML private JFXButton buttonExport;
    @FXML private JFXComboBox<String> comboBoxSubject;

    public ImportExportController() {
        mImport = new Import();
        mExport = new Export();
    }

    public void setModel(QuestionBank model) {
        mQuestionBank = model;
    }


    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void importAction(ActionEvent actionEvent) {
        mImport.importFromFile(actionEvent, mQuestionBank);
        // Display the just imported subject as available to export
        resetExportOptions();
    }

    /**
     * The user clicked the export button
     * @param actionEvent
     */
    public void exportAction(ActionEvent actionEvent) {
        // Get the selected subject object from the comboBoxSubject ComboBox
        Subject subject = mQuestionBank.getSubjectByName(comboBoxSubject.getValue());
        mExport.onClick(actionEvent, subject);
    }

    public void resetExportOptions() {
        // Checks if there is at least one subject in the QuestionBank
        if (mQuestionBank.getSubjects().isEmpty()) {
            buttonExport.setDisable(true);
            comboBoxSubject.setDisable(true);
        }
        else {
            buttonExport.setDisable(false);
            comboBoxSubject.setDisable(false);
            // Load each subject name from the QuestionBank into the combo box
            mQuestionBank.getSubjects()
                         .stream()
                         .filter(s -> !comboBoxSubject.getItems().contains(s.getSubjectName()))
                         .forEach(s -> {comboBoxSubject.getItems().add(s.getSubjectName());});

            // Set the preselected subject in the combo box as the first one
            comboBoxSubject.getSelectionModel().selectFirst();
        }
    }
}
