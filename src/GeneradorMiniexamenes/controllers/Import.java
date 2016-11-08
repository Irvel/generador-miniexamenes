package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Imports the subjects from either a .json file or from the .txt legacy
 * format to the model
 *
 * The .json file is imported each time the application is opened
 * The .txt is opened only when the user selects to import from the legacy
 * format from the menu
 */
public class Import {
    private Subject mSubject;

    /**
     * onClick
     *
     * Imports a set of questions from a single subject when importing from the
     * legacy .txt format or imports questions form a set of subjects when importing
     * from the new .json format.
     * TODO(Irvel): Clarify if each .json file should contain a single or multiple subjects
     * TODO(Irvel): Implement program startup import functionality
     *
     * @param ae actionEvent to get the Window
     */
    public QuestionBank onClick(ActionEvent ae, QuestionBank questionBank) {
        Node source = (Node) ae.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        File file = getFile(theStage);

        // Checks the extension of the file selected (txt or json)
        String extension = "";
        String name = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        // Get the filename without the extension
        name = file.getName().substring(0, i);

        if (extension.equals("txt")) {
            importFromText(file, name);
            return appendToModel(questionBank);
        }
        else {
            importFromJson(file, name);
            return replaceLoadedModel(questionBank);
        }
    }

    /**
     * getFile
     *
     * Loads a file containing a questionBank in either the .json or legacy .txt format
     *
     * @param currentStage The stage from which to launch the file chooser dialog
     */
    private File getFile(Stage currentStage) {
        // Opens file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu archivo (txt o json)");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".txt", "*.txt"),
                new FileChooser.ExtensionFilter(".json", "*.json")
        );
        return fileChooser.showOpenDialog(currentStage);
    }

    /**
     * appendToModel
     *
     * Appends the imported subject data into the working model
     *
     */
    private QuestionBank appendToModel(QuestionBank questionBank) {
        if (questionBank == null) {
            questionBank = new QuestionBank();
        }
        questionBank.addSubject(mSubject);
        mSubject = null; // Garbage Collector come to me
        return questionBank;
    }

    private QuestionBank replaceLoadedModel(QuestionBank questionBank) {
        return questionBank;
    }

    /**
     * importFromText
     *
     * method that lets us import miniExam from a txt file
     * Each .txt file contains only information from one subject.
     *
     * @param file Contains questions from one miniexam subject
     */
    private void importFromText(File file, String fileName) {
        // line and number of stars in a line
        String line;
        String questionName = "";
        int iSequence = 1;
        String answer;
        int iWeight;
        ArrayList<Block> blocks = new ArrayList<>();
        ArrayList<Question> questions = new ArrayList<>();
        ArrayList<Answer> answers = new ArrayList<>();
        // Tries to open the file
        try {
            Scanner in = new Scanner(new FileReader(file.getPath()));
            while (in.hasNextLine()) {
                line = in.nextLine();

                // found new block
                if (line.equals("**")) {
                    // ignores O
                    in.nextLine();
                    if (!questions.isEmpty()) { // if there are questions, adds the block
                        blocks.add(new Block(questions, iSequence++));
                    }
                    questions.clear();
                    answers.clear();
                }

                // found new question
                else if (line.equals("*")) {
                    if (!answers.isEmpty()) { // if there are answers, add the block
                        questions.add(new Question(answers, questionName));
                    }
                    answers.clear();
                    questionName = in.nextLine();
                }

                else{
                    iWeight = Integer.parseInt(line);
                    answer = in.nextLine();
                    answers.add(new Answer(answer, iWeight));
                }
            }
            // Checks if there is info needed to add
            if (!answers.isEmpty()) {
                questions.add(new Question(answers, questionName));
            }
            if (!questions.isEmpty()) {
                blocks.add(new Block(questions, iSequence));
            }

            // Creates the Subject
            this.mSubject = new Subject(blocks, fileName);

            // prints (for debugging) comment when done
            /*System.out.println("Subject " + mSubject.getmSubject());
            for (Block b : mSubject.getmBlocks()) {
                System.out.println(b.getSequenceNumber());
                for (Question q : b.getmQuestions()) {
                    System.out.println(q.getQuestion());
                    for (Answer a : q.getAnswers()) {
                        System.out.println(a.getWeight() + " " + a.getAnswer());
                    }
                }
            }*/

        } catch (FileNotFoundException e) {
            System.out.println("El archivo no pudo ser abierto");
        }
    }

    /**
     * importFromJson
     *
     * method that lets us import miniExam from a txt file
     * Each .json file may contain information from multiple subjects.
     *
     * @param file that contains miniexam
     */
    private void importFromJson(File file, String fileName){

    }

}
