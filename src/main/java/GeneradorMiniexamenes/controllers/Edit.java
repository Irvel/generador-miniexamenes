package GeneradorMiniexamenes.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;


/**
 * Created by Elias Mera on 11/21/2016.
 */
public class Edit {
    private MainController mParentController;
    private HBox mContainer;
    private boolean mFirstLoad;
    @FXML
    TableView tvExamBank;
    private final ObservableList<CellTable> data =
            FXCollections.observableArrayList(
                    new CellTable("Jacob", "Smith"),
                    new CellTable("Isabella", "Johnson"),
                    new CellTable("Ethan", "Williams"),
                    new CellTable("Emma", "Jones"),
                    new CellTable("Michael", "Brown"));


    /**
     * class constructor
     * @param mParentController
     */
    public Edit(MainController mParentController) {
        this.mParentController = mParentController;
        this.mFirstLoad = true;
    }

    /**
     * inflateViews
     *
     * Construct objects from the fxml view definitions and store them as member variables.
     */
    private void inflateViews() {
        try {
            // Delete any existing inflated views
            mContainer = null;

            // Load the form for generating exams but do not show it yet
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Edit.fxml"));
            loader.setController(this);
            mContainer = loader.load();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetFormFields(){
        //System.out.println("Entro");
        //tvExamBank.getItems().clear();
        tvExamBank.setEditable(true);

        tvExamBank.setItems(data);
    }

    public void loadGenerateForm(VBox mainContainer) {
        // If this is the first time loading the view
        if (mFirstLoad) {
            mFirstLoad = false;
            inflateViews();
            // Display the generate exams form view to the user
            mainContainer.getChildren().add(mContainer);
        }
        // Update the form options from the loaded ExamBank
        resetFormFields();
    }

    public static class CellTable{
        private final SimpleStringProperty sGrupo;
        private final SimpleStringProperty sExamen;

        public CellTable(String sGrupo, String sExamen) {
            this.sGrupo = new SimpleStringProperty(sGrupo);
            this.sExamen = new SimpleStringProperty(sExamen);
        }

        public String getsGrupo() {
            return sGrupo.get();
        }

        public String getsExamen() {
            return sExamen.get();
        }

        public void setsGrupo(String sGrupo) {
            this.sGrupo.set(sGrupo);
        }

        public void setsExamen(String sExamen) {
            this.sExamen.set(sExamen);
        }
    };
}
