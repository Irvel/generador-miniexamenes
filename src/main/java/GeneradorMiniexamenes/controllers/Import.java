package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static GeneradorMiniexamenes.controllers.Alerts.displayError;
import static GeneradorMiniexamenes.controllers.Alerts.displayInfo;

/**
 * Imports the subjects from either a .json file or from the .txt legacy
 * format to the model
 *
 * The .json file is imported each time the application is opened
 * The .txt is opened only when the user selects to import from the legacy
 * format from the menu
 */
public class Import {
    /**
     * generateExams
     *
     * Imports a set of questions from a single subject when importing from the
     * legacy .txt format or imports questions form a set of subjects when importing
     * from the new .json format.
     *
     * @param ae actionEvent to get the Window
     */
    public QuestionBank onClick(ActionEvent ae, QuestionBank questionBank) {
        Node source = (Node) ae.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        File file = getFile(theStage);
        // The user selected the "cancel" option
        if (file == null) {
            return null;
        }
        // Checks the extension of the file selected (txt or json)
        String extension = "";
        String name = "";
        int dotIdx = file.getName().lastIndexOf('.');
        if (dotIdx > 0) {
            extension = file.getName().substring(dotIdx + 1);
        }
        // Get the filename without the extension
        name = file.getName().substring(0, dotIdx).trim();

        if (extension.equals("txt")) {
            Subject imported = importFromText(file, name);
            return appendToModel(imported, questionBank, name);
        }
        else {
            Subject imported = importFromJson(file);
            return appendToModel(imported, questionBank, name);
        }
    }

    /**
     * getFile
     *
     * Loads a file containing a questionBank in either the .json or legacy .txt format
     *
     * @param currentStage The stage from which to launch the file chooser dialog
     * @return fileChooser The user-selected file to import from
     */
    private File getFile(Stage currentStage) {
        // Opens file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu archivo (txt o json)");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );
        return fileChooser.showOpenDialog(currentStage);
    }

    /**
     * appendToModel
     *
     * Appends the imported subject data into the working model. If there already exists a
     * subject with the given name, replace the existing subject with the imported one.
     *
     */
    private QuestionBank appendToModel(Subject imported,
                                       QuestionBank questionBank,
                                       String filename) {
        if (imported == null) {
            if (questionBank == null) {
                questionBank = new QuestionBank();
            }
            displayError("Error al importar",
                         "No fue posible importar el tema del archivo seleccionado. El " +
                                           "banco de preguntas actual permanecerá sin cambios");
        }
        else {
            // Ensure that the name of the imported subject is the same as it's containing filename
            imported.setSubjectName(filename);
            displayInfo("Se ha agregado el tema " + filename + " al banco de " +
                                          "preguntas.");
            questionBank.addSubject(imported);
        }
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
    private Subject importFromText(File file, String fileName) {
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
                        if (!answers.isEmpty()) { // if there are answers, add the block
                            questions.add(new Question(new ArrayList<>(answers), questionName));
                        }
                        blocks.add(new Block(new ArrayList<>(questions), iSequence++));
                        questions.clear();
                        answers.clear();
                    }
                }

                // found new question
                else if (line.equals("*")) {
                    if (!answers.isEmpty()) { // if there are answers, add the block
                        questions.add(new Question(new ArrayList<>(answers), questionName));
                        answers.clear();
                    }
                    questionName = in.nextLine();
                }

                else {
                    iWeight = Integer.parseInt(line);
                    answer = in.nextLine();
                    answers.add(new Answer(answer, iWeight));
                }
            }
            // Checks if there is info needed to be added
            if (!answers.isEmpty()) {
                questions.add(new Question(new ArrayList<>(answers), questionName));
            }
            if (!questions.isEmpty()) {
                blocks.add(new Block(new ArrayList<>(questions), iSequence));
            }

            // Creates the Subject
            return new Subject(blocks, fileName);

        } catch (FileNotFoundException e) {
            System.out.println("El archivo no pudo ser abierto");
        }
        return null;
    }

    /**
     * importFromJson
     *
     * Import a set of subjects with questions from a .json file
     *
     * @param file The json file to import
     */
    private Subject importFromJson(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(file, Subject.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
