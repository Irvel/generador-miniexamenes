package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.QuestionBank;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FileOutputStream outFile = new FileOutputStream(QUESTIONS_PATH, false);
            objectMapper.writeValue(outFile, questionBank);
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FileOutputStream outFile = new FileOutputStream(EXAMS_PATH, false);
            objectMapper.writeValue(outFile, examBank);
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(EXAMS_PATH);
            return mapper.readValue(file, ExamBank.class);
        }
        catch (IOException e) {
            System.out.println("Creating an empty ExamBank");
        }
        return new ExamBank();
    }
}