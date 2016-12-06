package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.Group;
import GeneradorMiniexamenes.model.Options;
import GeneradorMiniexamenes.model.QuestionBank;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Handles data persistence on the application. Is responsible of restoring a previous state and
 * saving the current one.
 *
 */
public class AppState {
    static final String QUESTIONS_PATH = "data/QuestionBank.json";
    static final String EXAMS_PATH = "data/ExamBank.json";
    static final String OPTIONS_PATH = "data/Options.json";

    /**
     * loadQuestionBank
     *
     * Import the main question bank for the application. If it doesn't exist, create a new one.
     *
     * @return The question bank stored inside the Application
     */
    public static QuestionBank loadQuestionBank(String path) {
        checkDataDirectoryExists();
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(path);
            return mapper.readValue(file, QuestionBank.class);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Creating an empty QuestionBank");
        }
        return new QuestionBank();
    }

    /**
     * saveQuestionBank
     *
     * Save the current question bank to persistent Storage in QUESTIONS_PATH.
     * @param questionBank The question bank to be saved
     */
    public static void saveQuestionBank(QuestionBank questionBank, String path) {
        runInBackground(() -> {
            checkDataDirectoryExists();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                FileOutputStream outFile = new FileOutputStream(path, false);
                objectMapper.writeValue(outFile, questionBank);
                // Yes this is hacky. But for some reason sometimes Jackson doesn't output an
                // endline at the end of the file, so when it tries to read it again, it crashes.
                objectMapper.writeValueAsString("\n");
                outFile.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loadExamBank
     *
     * Import the previously generated Exams from the application
     *
     * @return The previously generated exams
     */
    public static ExamBank loadExamBank(String path) {
        checkDataDirectoryExists();
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(path);
            return new ExamBank(mapper.readValue(file, new TypeReference<HashMap<String,
                    ArrayList<Group>>>(){}));
        }
        catch (IOException e) {
            System.out.println("Creating an empty ExamBank");
        }
        return new ExamBank();
    }

    /**
     * saveExamBank
     *
     * Save the current exam bank to persistent Storage in EXAMS_PATH.
     *
     * @param examBank The exam bank to be saved
     */
    public static void saveExamBank(ExamBank examBank) {
        runInBackground(() -> {
            checkDataDirectoryExists();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                FileOutputStream outFile = new FileOutputStream(EXAMS_PATH, false);
                objectMapper.writeValue(outFile, examBank.getGroups());
                outFile.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * loadOptions
     *
     * Import general options for the application
     *
     */
    public static Options loadOptions(String path) {
        checkDataDirectoryExists();
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(path);
            return mapper.readValue(file, Options.class);
        }
        catch (IOException e) {
            System.out.println("Initializing with default options");
        }
        return new Options();
    }

    /**
     * saveOptions
     *
     * Save general options for the application
     *
     */
    public static void saveOptions(Options options) {
        runInBackground(() -> {
            checkDataDirectoryExists();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                FileOutputStream outFile = new FileOutputStream(OPTIONS_PATH, false);
                objectMapper.writeValue(outFile, options);
                outFile.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void runInBackground(Runnable function) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            try{
                                function.run();
                            }finally{
                                latch.countDown();
                            }
                        });
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    /**
     * checkDataDirectoryExists
     *
     * Check if the data directory exists. If it doesn't, create it.
     *
     */
    private static void checkDataDirectoryExists() {
        File dataPath = new File("data");
        if (dataPath.exists()) {
            return;
        }
        dataPath.mkdir();
    }
}
