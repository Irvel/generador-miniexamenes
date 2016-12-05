package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Options;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static GeneradorMiniexamenes.controllers.AlertMaker.displayError;
import static GeneradorMiniexamenes.controllers.AlertMaker.displayInfo;

/**
 * OptionsController
 *
 * Modify general options from the program..
 * TODO: Add the ability to delete groups
 */
public class OptionsController {
    private Options mOptions;

    @FXML private JFXTextField textFieldAddGroup;
    @FXML private JFXTextField textFieldHeader;

    public OptionsController() {
    }

    @FXML private void initialize() {
        textFieldHeader.textProperty()
                       .addListener((ov, t, t1) -> mOptions.setClassTitle(textFieldHeader.getText().trim()));
    }

    public void setModel(Options options) {
        mOptions = options;
    }

    public void loadForm() {
        textFieldHeader.setText(mOptions.getClassTitle());
    }

    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void addGroupAction(ActionEvent actionEvent) {
        if (textFieldAddGroup == null || textFieldAddGroup.getText().trim().equalsIgnoreCase("")) {
            displayError("Error", "Favor de ingresar un grupo.");
            return;
        }
        mOptions.addGroup(textFieldAddGroup.getText().trim());
        displayInfo("Se ha agregado el grupo " + textFieldAddGroup.getText() + ".");
        textFieldAddGroup.setText("");
    }

}
