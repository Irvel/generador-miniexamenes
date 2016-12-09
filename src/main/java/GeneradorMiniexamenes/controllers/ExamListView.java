package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.Group;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

/**
 * Created by Irvel on 12/3/16.
 */
public class ExamListView {
    private JFXListView<Exam> mExamListView;

    public ExamListView(AnchorPane parentContainer, ChangeListener examListListener,
                        EventHandler<MouseEvent> mouseVersion) {
        mExamListView = new JFXListView<Exam>();
        mExamListView.getSelectionModel().selectedItemProperty().addListener(examListListener);
        // Use an additional event listener for mouse movement because on Windows it sometimes
        // misses the above one
        mExamListView.setOnMouseMoved(mouseVersion);

        AnchorPane.setLeftAnchor(mExamListView, 0.0);
        AnchorPane.setRightAnchor(mExamListView, 0.0);
        AnchorPane.setTopAnchor(mExamListView, 0.0);
        AnchorPane.setBottomAnchor(mExamListView, 0.0);
        mExamListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        parentContainer.getChildren().add(mExamListView);
    }

    public ExamListView(AnchorPane parentContainer, ChangeListener examListListener) {
        mExamListView = new JFXListView<Exam>();
        mExamListView.getSelectionModel().selectedItemProperty().addListener(examListListener);
        mExamListView.setPlaceholder(new Label("No existe ningún exámen generado."));
        AnchorPane.setLeftAnchor(mExamListView, 0.0);
        AnchorPane.setRightAnchor(mExamListView, 0.0);
        AnchorPane.setTopAnchor(mExamListView, 0.0);
        AnchorPane.setBottomAnchor(mExamListView, 0.0);
        mExamListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    public ArrayList<Exam> getSelectedExams() {
        if (mExamListView.getSelectionModel().getSelectedItems() != null) {
            return new ArrayList<Exam>(mExamListView.getSelectionModel().getSelectedItems());
        }
        return null;
    }

    public void clear() {
        mExamListView.getItems().clear();
    }
}
