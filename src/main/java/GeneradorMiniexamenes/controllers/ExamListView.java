package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.Group;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;

/**
 * Created by Irvel on 12/3/16.
 */
public class ExamListView {
    private JFXListView<Exam> mExamListView;

    public ExamListView(AnchorPane parentContainer, ChangeListener examListListener) {
        mExamListView = new JFXListView<Exam>();
        mExamListView.getSelectionModel().selectedItemProperty().addListener(examListListener);
        AnchorPane.setLeftAnchor(mExamListView, 0.0);
        AnchorPane.setRightAnchor(mExamListView, 0.0);
        AnchorPane.setTopAnchor(mExamListView, 0.0);
        AnchorPane.setBottomAnchor(mExamListView, 0.0);
        parentContainer.getChildren().add(mExamListView);
    }

    public ExamListView(AnchorPane parentContainer) {
        mExamListView = new JFXListView<Exam>();
        AnchorPane.setLeftAnchor(mExamListView, 0.0);
        AnchorPane.setRightAnchor(mExamListView, 0.0);
        AnchorPane.setTopAnchor(mExamListView, 0.0);
        AnchorPane.setBottomAnchor(mExamListView, 0.0);
        mExamListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //listView.getSelectionModel().getSelectedItems()
        parentContainer.getChildren().add(mExamListView);
    }

    /**
     * refreshListView
     *
     * Delete any existing ListView and create a new one with the exams from the selected subject
     * and group.
     *
     */
    public void refreshListView(Group group) {
        mExamListView.getItems().clear();
        for (Exam exam : group.getExams()) {
            mExamListView.getItems().add(exam);
        }
    }

    // TODO: Add support for selecting multiple exams
    public Exam getSelectedExam() {
        return mExamListView.getSelectionModel().getSelectedItem();
    }
}
