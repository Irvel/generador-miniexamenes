package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomly generates exams from the subjects and questions available in the
 * model.
 */
public class Generate {

    public Generate() {
    }


    /**
     * Generates exams randomly
     * @param actionEvent ae
     * @param subject exam subject
     * @param iQuantity quantity to be generated
     * @param group group
     * @return examBank
     */
    public ExamBank onClick(ActionEvent actionEvent, Subject subject, int iQuantity, String group){
        ArrayList<Question> mQuestions = new ArrayList<>();
        Exam mExam;
        ExamBank mExBank = new ExamBank();

        // Generates iQuantity exams
        for(int i = 0; i < iQuantity; i++){
            // for each block in the subject gets a random question
            for(Block b : subject.getBlocks()){
                mQuestions.add(b.getQuestions().get(ThreadLocalRandom.current().nextInt(0, b.getQuestions().size())));
            }

            // creates exam
            mExam = new Exam(subject.getSubjectName(), group, new ArrayList<>(mQuestions));
            // adds exam
            mExBank.addExam(subject.getSubjectName(), mExam);
            // cleans questions
            mQuestions.clear();
        }

        if (mExBank == null)
            alertUnsuccessfulGenerate("Examenes no generados");
        else
            alertSuccessfulGenerate("Examenes generados");

        return mExBank;
    }

    /**
     * alertSuccessfulGenerate
     *
     * Inform the user of a successful import operation
     *
     * @param s Information on what was successfully imported and its effect
     */
    private void alertSuccessfulGenerate(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    /**
     * alertUnsuccessfulGenerate
     *
     * Inform the user of a failed import operation
     *
     * @param s Information on what made the error occurr and what will be done
     */
    private void alertUnsuccessfulGenerate(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al generar");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

}
