package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.fasterxml.jackson.core.JsonParser;
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

import static GeneradorMiniexamenes.controllers.AlertMaker.displayError;
import static GeneradorMiniexamenes.controllers.AlertMaker.displayInfo;

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
     * importFromFile
     *
     * Imports a set of questions from a single subject when importing from the
     * legacy .txt format or imports questions form a set of subjects when importing
     * from the new .json format.
     *
     * @param ae actionEvent to get the Window
     */
    public void importFromFile(ActionEvent ae, QuestionBank questionBank) {
        File file = getFile(ae);
        // The user selected the "cancel" option
        if (file == null) {
            return;
        }
        String extension = getExtension(file.getName());
        String name = getFileName(file.getName());

        // Checks the extension of the file selected (txt or json)
        if (extension.equals("txt")) {
            Subject imported = importFromText(file, name);
            // Ensure that the name of the imported subject is the same as it's containing
            // filename only for text files
            if (imported != null) {
                imported.setSubjectName(name);
            }
            appendToModel(imported, questionBank, name);
        }
        else {
            Subject imported = importFromJson(file);
            appendToModel(imported, questionBank, name);
        }
    }

    private String getExtension(String name) {
        String extension = "";
        int dotIdx = name.lastIndexOf('.');
        if (dotIdx > 0) {
            extension = name.substring(dotIdx + 1);
        }
        return extension;
    }

    // Get the filename without the extension
    private String getFileName(String longName) {
        return longName.substring(0, longName.lastIndexOf('.')).trim();
    }

    private File getFile(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        return getFile(theStage);
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
     * TODO: Use the name of the json instead of the filename when using json import
     *
     */
    private void appendToModel(Subject imported, QuestionBank questionBank, String filename) {
        if (imported == null) {
            displayError("Error al importar",
                         "No fue posible importar el tema del archivo seleccionado. El " +
                                           "banco de preguntas actual permanecerá sin cambios");
        }
        else {
            // Clean any trailing user-left spaces
            imported.setSubjectName(imported.getSubjectName().trim());
            displayInfo("Se ha agregado el tema " + imported.getSubjectName() + " al banco de preguntas.");
            questionBank.addSubject(imported);
        }
        AppState.saveQuestionBank(questionBank, AppState.QUESTIONS_PATH);
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
                        blocks.add(new Block(new ArrayList<>(questions)));
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
                blocks.add(new Block(new ArrayList<>(questions)));
            }

            // Creates the Subject
            return new Subject(fileName, blocks);

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
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        try {
            return mapper.readValue(file, Subject.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
