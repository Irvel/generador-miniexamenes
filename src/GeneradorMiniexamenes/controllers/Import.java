package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Answer;
import GeneradorMiniexamenes.model.Block;
import GeneradorMiniexamenes.model.Question;
import GeneradorMiniexamenes.model.Subject;
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

    private ArrayList<Block> mBlocks = new ArrayList<>();
    private ArrayList<Question> mQuestions = new ArrayList<>();
    private ArrayList<Answer> mAnswers = new ArrayList<>();
    private Subject mSubject;

    /**
     * onClick
     *
     * method that opens a file to import data
     *
     * @param ae actionEvent to get the Window
     */
    public void onClick(ActionEvent ae){
        Node source = (Node) ae.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();

        // Opens file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona tu archivo (txt o json)");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(theStage);

        // Checks the extension of the file selected (txt or json)
        String extension = "";
        String name = "";
        int i = file.getName().lastIndexOf('.');
        if(i > 0)
            extension = file.getName().substring(i + 1);
        name = file.getName().substring(0, i);

        if(extension.equals("txt"))
            importFromText(file, name);
        else
            importFromJson(file, name);
    }

    /**
     * importFromText
     *
     * method that lets us import miniExam from a txt file
     *
     * @param file that contains miniexam
     */
    private void importFromText(File file, String fileName){
        // line and number of stars in a line
        String line;
        String questionName = "";
        int iSequence = 1;
        String answer;
        int iWeight;
        // Tries to open the file
        try {
            Scanner in = new Scanner(new FileReader(file.getPath()));
            while(in.hasNextLine()){
                line = in.nextLine();

                // found new block
                if(line.equals("**")){
                    // ignores O
                    in.nextLine();
                    if(!mQuestions.isEmpty()){ // if there are questions, adds the block
                        this.mBlocks.add(new Block(mQuestions, iSequence++));
                    }
                    mQuestions.clear();
                    mAnswers.clear();
                }

                // found new question
                else if(line.equals("*")){
                    if(!mAnswers.isEmpty()){ // if there are answers, add the block
                        this.mQuestions.add(new Question(mAnswers, questionName));
                    }
                    mAnswers.clear();
                    questionName = in.nextLine();
                }

                else{
                    iWeight = Integer.parseInt(line);
                    answer = in.nextLine();
                    mAnswers.add(new Answer(answer, iWeight));
                }
            }
            // Checks if there is info needed to add
            if(!mAnswers.isEmpty())
                this.mQuestions.add(new Question(mAnswers, questionName));
            if(!mQuestions.isEmpty())
                this.mBlocks.add(new Block(mQuestions, iSequence));

            // Creates the Subject
            this.mSubject = new Subject(mBlocks, fileName);

            // prints (for debugging) comment when done
            System.out.println("sequence " + mSubject.getmSubject());
            for(Block b : mSubject.getmBlocks()){
                System.out.println(b.getSequenceNumber());
                for(Question q : b.getmQuestions()){
                    System.out.println(q.getQuestion());
                    for(Answer a : q.getAnswers()){
                        System.out.println(a.getWeight() + " " + a.getAnswer());
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("El archivo no pudo ser abierto");
        }
    }

    private void importFromJson(File file, String fileName){

    }

}
