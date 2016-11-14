package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static GeneradorMiniexamenes.controllers.Alerts.displayError;
import static GeneradorMiniexamenes.controllers.Alerts.displayInfo;

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
            displayError("Error al generar", "Examenes no generados");
        else
            displayInfo("Examenes generados");

        return mExBank;
    }

}
