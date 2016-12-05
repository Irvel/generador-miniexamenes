package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamTemplate;
import GeneradorMiniexamenes.model.GroupList;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static GeneradorMiniexamenes.controllers.AlertMaker.displayInfo;

/**
 * OptionsController
 *
 * Modify general options from the program..
 * TODO: Add the ability to delete groups
 */
public class OptionsController {
    private GroupList mGroupList;

    @FXML private JFXTextField textFieldAddGroup;
    @FXML private JFXTextField textFieldHeader;

    public OptionsController() {
    }

    @FXML private void initialize() {
        textFieldHeader.setText(ExamTemplate.className);
        textFieldHeader.textProperty()
                       .addListener((ov, t, t1) -> ExamTemplate.className = textFieldHeader.getText().trim());
    }

    public void setModel(GroupList groupList) {
        mGroupList = groupList;
    }

    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void addGroupAction(ActionEvent actionEvent) {
        mGroupList.addGroup(textFieldAddGroup.getText().trim());
        displayInfo("Se ha agregado el grupo " + textFieldAddGroup.getText() + ".");
        textFieldAddGroup.setText("");
    }

}
