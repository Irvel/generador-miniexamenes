package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Options;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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

    @FXML private JFXTextField textFieldHeader;
    @FXML private JFXTextField textFieldAddGroup;
    @FXML private JFXComboBox<String> comboBoxRemoveGroup;
    @FXML private JFXButton buttonRemoveGroup;

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
        textFieldAddGroup.setText("");
        comboBoxRemoveGroup.getItems().clear();
        if (mOptions.getGroupNames().size() <= 0) {
            comboBoxRemoveGroup.setDisable(true);
            buttonRemoveGroup.setDisable(true);
        }
        else {
            comboBoxRemoveGroup.getItems().addAll(mOptions.getGroupNames());
            comboBoxRemoveGroup.getSelectionModel().selectFirst();
            comboBoxRemoveGroup.setDisable(false);
            buttonRemoveGroup.setDisable(false);
        }
    }

    /**
     * The user clicked the add group button
     * @param actionEvent
     */
    public void addGroupAction(ActionEvent actionEvent) {
        if (textFieldAddGroup == null || textFieldAddGroup.getText().trim().equalsIgnoreCase("")) {
            displayError("Error", "Favor de ingresar un grupo.");
            return;
        }
        mOptions.addGroup(textFieldAddGroup.getText().trim());
        displayInfo("Se ha agregado el grupo " + textFieldAddGroup.getText() + ".");
        loadForm();
    }

    /**
     * The user clicked the remove group button
     * @param actionEvent
     */
    public void removeGroupAction(ActionEvent actionEvent) {
        if (comboBoxRemoveGroup == null || comboBoxRemoveGroup.getSelectionModel()
                                                              .getSelectedItem()
                                                              .equalsIgnoreCase("")) {
            displayError("Error", "Favor de seleccionar un grupo.");
            return;
        }
        String toDelete = comboBoxRemoveGroup.getSelectionModel().getSelectedItem();
        mOptions.removeGroup(toDelete);
        displayInfo("Se ha eliminado el grupo " + toDelete + " de las posibles opciones para " +
                            "generar exámenes.");
        loadForm();
    }

}
