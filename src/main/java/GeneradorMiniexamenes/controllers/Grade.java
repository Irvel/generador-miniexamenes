package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Elias Mera on 11/20/2016.
 */
public class Grade {
    private MainController mParentController;
    private boolean mFirstLoad;

    @FXML JFXComboBox<String> cbTemaG;
    @FXML JFXComboBox<String> cbGrupoG;

    /**
     * Constructor de la clase
     */
    public Grade() {
        this.mFirstLoad = true;
    }

    /**
     * Method to keep updating form fields
     * loads combobox
     */
    private void resetFormFields(){
        // checks if the Exam bank is not empy
        if(mParentController.getExamBank().getExams().isEmpty()){
            cbTemaG.setDisable(true);
            cbGrupoG.setDisable(true);
        }
        else{
            cbTemaG.setDisable(false);
            cbGrupoG.setDisable(false);
            HashMap<String, ArrayList<Exam>> mExams = mParentController.getExamBank().getExams();
            Iterator it = mExams.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry e = (Map.Entry)it.next();
                System.out.println(e.getKey().toString());
            }
        }
    }

    /**
     * loadGradeForm
     *
     * Method called every time the user selects the Grading tab
     */
    public void loadGradeForm() throws IOException {
        if(mFirstLoad){
            mFirstLoad = false;
        }
        resetFormFields();
    }
}
