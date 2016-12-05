package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static GeneradorMiniexamenes.controllers.AppState.loadQuestionBank;
import static org.junit.Assert.*;

/**
 * Created by Irvel on 12/2/16.
 */
public class GenerateExamsControllerTest {
    private GenerateExamsController mGenerateController;
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private Options mOptions;
    private final int mQuantityToGenerate = 3;

    @Before
    public void setUp() {
        mQuestionBank = loadQuestionBank("src/test/resources/dummyQuestionBank.json");
        mExamBank = new ExamBank();
        mOptions = new Options();
        mGenerateController = new GenerateExamsController(mQuestionBank, mExamBank, mOptions);
    }

    @Test
    public void testGenerateTwoExams() {
        Group firstExam = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"), 1, "testGroup");
        assertEquals(firstExam.getExams().size(), 1);
        assertEquals(1, firstExam.getExams().get(0).getExamNumber());
        mExamBank.addGroup("formulas", firstExam);
        Group secondExam = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"), 1, "testGroup");
        assertEquals(secondExam.getExams().size(), 1);
        assertEquals(2, secondExam.getExams().get(0).getExamNumber());
    }

    @Test
    public void testGenerate1FromPreviousQuantity() {
        Group generatedExams = mGenerateController
                .generateExams(mQuestionBank.getSubjectByName("formulas"), mQuantityToGenerate, "testGroup");
        assertEquals(generatedExams.getExams().size(), mQuantityToGenerate);
        mExamBank.addGroup("formulas", generatedExams);
        Group theOne = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"), 1, "testGroup");
        assertEquals(theOne.getExams().size(), 1);
        assertEquals(mQuantityToGenerate + 1, theOne.getExams().get(0).getExamNumber());
    }

    @Test
    public void testGenerateNFromOne() {
        Group firstExam = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"), 1, "testGroup");
        mExamBank.addGroup("formulas", firstExam);
        Group generatedExams = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"),
                                                                 mQuantityToGenerate,
                                                                 "testGroup");
        assertEquals(mQuantityToGenerate, generatedExams.getExams().size());
        int expectedNumber = 2;
        for (Exam exam : generatedExams.getExams()) {
            // Asserts the exam number was generated from mQuantityToGenerate to
            // mQuantityToGenerate * 2
            assertEquals(expectedNumber, exam.getExamNumber());
            expectedNumber++;
        }
    }

    @Test
    public void testGenerateNFromPreviousQuantity() {
        Group firstGroup = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"),
                                                             mQuantityToGenerate,
                                                             "testGroup");
        mExamBank.addGroup("formulas", firstGroup);
        Group generatedExams = mGenerateController.generateExams(mQuestionBank.getSubjectByName("formulas"),
                                                                 mQuantityToGenerate,
                                                                 "testGroup");
        assertEquals(mQuantityToGenerate, generatedExams.getExams().size());
        int expectedNumber = mQuantityToGenerate + 1;
        for (Exam exam : generatedExams.getExams()) {
            // Asserts the exam number was generated from mQuantityToGenerate to
            // mQuantityToGenerate * 2
            assertEquals(expectedNumber, exam.getExamNumber());
            expectedNumber++;
        }
    }

    @Test
    public void testCreateTempDirectory() {
        final String testDir = ".testDirectory1";
        if (new File(testDir + File.separator + "testDirectory2").mkdirs()) {
            // Create a directory when an existing one with files was already there
            assertTrue(mGenerateController.tempDirectorySetup(testDir));
            File createdDir = new File(testDir);
            assertTrue("The test directory was in fact created",createdDir.exists());
            assertTrue("The test directory is a directory", createdDir.isDirectory());
            assertEquals("The test directory is empty", createdDir.list().length, 0);
            assertTrue("The test directory is hidden", createdDir.isHidden());
            createdDir.deleteOnExit();
        }
    }

    @Test
    public void testRemoveTempDirectory() {
        final String parentTestDir = ".testDirectory1";
        final String nestedDirs = parentTestDir + File.separator + "testDirectory2" +
                File.separator + "testDirectory3" + File.separator + "testDirectory4";

        if (new File(nestedDirs).mkdirs()) {
            File createdDir = new File(parentTestDir);
            mGenerateController.removeDirectory(createdDir);
            assertFalse("The test directory doesn't exist", createdDir.exists());
        }
    }
}
