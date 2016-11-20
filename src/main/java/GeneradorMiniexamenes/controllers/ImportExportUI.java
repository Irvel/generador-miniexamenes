package GeneradorMiniexamenes.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by Irvel on 11/19/16.
 */
public class ImportExportUI {
    private MainController mParentController;
    private Import mImport;
    private Export mExport;
    private boolean mFirstLoad;

    public ImportExportUI(MainController parentController) {
        // Keep a reference to the main controller to get important shared variables
        mParentController = parentController;
        mImport = new Import();
        mExport = new Export();
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
        mExport.onClick(actionEvent, mParentController.getQuestionBank());
    }

    public void loadImportExport(VBox mainContainer) {
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
}
