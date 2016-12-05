package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamTemplate;
import GeneradorMiniexamenes.model.GroupList;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * ImportExportController
 *
 * This class is the view controller for the ImportExport exams interface. It is responsible for
 * inflating the fxml into an object and adding it to the displayed layout. It is also the one
 * that handles actions inside the view.
 */
public class OptionsController {
    private GroupList mGroupList;

    @FXML private JFXTextField textFieldAddGroup;

    public OptionsController() {
    }

    public void setModel(QuestionBank model) {
        mQuestionBank = model;
    }

    public void setModel(GroupList groupList) {
        mGroupList = groupList;
    }

    /**
     * The user clicked the import button
     * @param actionEvent
     */
    public void importAction(ActionEvent actionEvent) {
    }

}
