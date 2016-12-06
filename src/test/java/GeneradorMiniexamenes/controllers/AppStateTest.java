package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.Main;
import GeneradorMiniexamenes.model.*;
import javafx.application.Application;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

/**
 * Created by Irvel on 12/5/16.
 */
public class AppStateTest {
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private Options mOptions;
    private String questionBankJson = "{\"subjects\":[{\"subjectName\":\"testSubject\"," +
            "\"blocks\":[{\"questions\":[{\"answers\":[{\"answer\":\"testAnswerA\"," +
            "\"weight\":100},{\"answer\":\"testAnswerB\",\"weight\":43}," +
            "{\"answer\":\"testAnswerC\",\"weight\":10},{\"answer\":\"testAnswerD\"," +
            "\"weight\":0}],\"question\":\"testQuestion1\"}]}]}]}";

    // Make the tests use the JavaFx Thread
    //@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() {
        mQuestionBank = new QuestionBank();
        ArrayList<Block> testBlocks = new ArrayList<>();
        ArrayList<Question> testQuestions = new ArrayList<>();
        ArrayList<Answer> testAnswers = new ArrayList<>();

        testAnswers.add(new Answer("testAnswerA", 100));
        testAnswers.add(new Answer("testAnswerB", 43));
        testAnswers.add(new Answer("testAnswerC", 10));
        testAnswers.add(new Answer("testAnswerD", 0));
        testQuestions.add(new Question(testAnswers, "testQuestion1"));
        testBlocks.add(new Block(testQuestions));
        mQuestionBank.addSubject(new Subject("testSubject", testBlocks));
        mExamBank = new ExamBank();
        mOptions = new Options();
        System.out.printf("Launching the Java FX App thread\n");
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(Main.class);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Test
    public void testSaveQuestionBank() {
        String testFilename = "testQuestionBank";
        AppState.saveQuestionBank(mQuestionBank, testFilename);
        File testFile = new File(testFilename);
        // Because saveQuestionBank creates a background task to save the QuestionBank, we need
        // to wait until that thread is done to be able to proceed with the test.
        await().atMost(3, SECONDS).until(testFile::exists);
        String readTestJson = readTestFile(testFilename);
        assertEquals(questionBankJson, readTestJson);
        testFile.deleteOnExit();
    }

    @Test
    public void testSaveThenLoad() {
        String testFilename = "testQuestionBank.json";
        File testFile = new File(testFilename);
        AppState.saveQuestionBank(mQuestionBank, testFilename);
        await().atMost(3, SECONDS).until(testFile::exists);

        QuestionBank testQuestionBank = AppState.loadQuestionBank(testFilename);
        await().atMost(3, SECONDS).until(() -> testQuestionBank != null);

        assertEquals(mQuestionBank.getSubjects().get(0).getSubjectName(),
                     testQuestionBank.getSubjects().get(0).getSubjectName());
        testFile.deleteOnExit();
    }

    private String readTestFile(String filename) {
        String readTestJson = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            br.close();
            readTestJson = sb.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return readTestJson;
    }
}
