package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.QuestionBank;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles data persistence on the application. Is responsible of restoring a previous state and
 * saving the current one.
 *
 */
public class AppState {
    private static final String QUESTIONS_PATH = "data/QuestionBank.json";
    private static final String EXAMS_PATH = "data/ExamBank.json";

    /**
     * loadQuestionBank
     *
     * Import the main question bank for the application. If it doesn't exist, create a new one.
     *
     * @return The question bank stored inside the Application
     */
    public static QuestionBank loadQuestionBank() {
        checkDataExists();
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(QUESTIONS_PATH);
            return mapper.readValue(file, QuestionBank.class);
        }
        catch (IOException e) {
            System.out.println("Creating an empty QuestionBank");
        }
        return new QuestionBank();
    }

    /**
     * saveQuestionBank
     *
     * Save the current question bank to persistent Storage in QUESTIONS_PATH.
     *
     * @param questionBank The question bank to be saved
     */
    public static void saveQuestionBank(QuestionBank questionBank) {
        checkDataExists();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FileOutputStream outFile = new FileOutputStream(QUESTIONS_PATH, false);
            objectMapper.writeValue(outFile, questionBank);
            outFile.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * saveExamBank
     *
     * Save the current exam bank to persistent Storage in EXAMS_PATH.
     *
     * @param examBank The exam bank to be saved
     */
    public static void saveExamBank(ExamBank examBank) {
        checkDataExists();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FileOutputStream outFile = new FileOutputStream(EXAMS_PATH, false);
            objectMapper.writeValue(outFile, examBank.getExams());
            outFile.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loadExamBank
     *
     * Import the previously generated Exams from the application
     *
     * @return The previously generated exams
     */
    public static ExamBank loadExamBank() {
        checkDataExists();
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(EXAMS_PATH);
            return new ExamBank(mapper.readValue(file, new TypeReference<HashMap<String,
                    ArrayList<Exam>>>(){}));
        }
        catch (IOException e) {
            System.out.println("Creating an empty ExamBank");
        }
        return new ExamBank();
    }

    /**
     * checkDataExists
     *
     * Check if the data directory exists. If it doesn't, create it.
     *
     */
    private static void checkDataExists() {
        File dataPath = new File("data");
        if (dataPath.exists()) {
            return;
        }
        dataPath.mkdir();
    }
}
