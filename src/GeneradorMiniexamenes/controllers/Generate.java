package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.Exam;
import GeneradorMiniexamenes.model.ExamBank;
import GeneradorMiniexamenes.model.Question;
import GeneradorMiniexamenes.model.Subject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static GeneradorMiniexamenes.controllers.Alerts.displayInfo;

/**
 * Randomly generates exams from the subjects and questions available in the
 * model.
 * TODO: ADD A BACK BUTTON THAT RESETS THE WHOLE THING
 */
public class Generate {
    private MainController mParentController;

    @FXML
    JFXComboBox<String> cbTema;

    @FXML
    Spinner spCantidad;

    @FXML
    JFXButton btnGenerate;

    @FXML
    JFXTextField tfGrupo;

    private HBox generateContainer;
    private HBox downloadContainer;
    private boolean firstLoad;


    public Generate(MainController parentController) {
        // Keep a reference to the main controller to get important shared variables
        firstLoad = true;
        mParentController = parentController;
    }

    private void inflateViews() {
        try {
            // Load the form for generating exams but do not show it yet
            generateContainer = null;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GenExamsAction.fxml"));
            loader.setController(this);
            generateContainer = loader.load();

            // Load the interface for downloading the Generated exams but do not show it yet
            downloadContainer = null;
            downloadContainer = FXMLLoader.load(
                    getClass().getResource("/fxml/GenExamsDownload.fxml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The user clicked the generate button
     * @param actionEvent
     */
    public void generateAction(ActionEvent actionEvent) {
        if (areFieldsInvalid()) {
            return;
        }

        // Get the selected subject object from the cbTema ComboBox
        Subject subject = mParentController.getQuestionBank()
                                           .getSubjectByName(cbTema.getValue());
        int examQuantity = Integer.parseInt(spCantidad.getValue().toString());
        ExamBank generatedExamBank = generateExams(subject,
                                                   examQuantity,
                                                   tfGrupo.getText());

        // Add the newly generated exams to the list of generated exams for this subject
        mParentController.getExamBank().appendExamBank(generatedExamBank);
        displayGeneratedExams(generatedExamBank);
    }

    /**
     * displayGeneratedExams
     *
     * Display the generated exams in another view and show the options for downloading the
     * generated exams.
     *
     * @param generatedExamBank The set of generated that is to be shown to the user
     *
     */
    private void displayGeneratedExams(ExamBank generatedExamBank) {
        VBox mainGenContainer = (VBox) generateContainer.getParent();
        displayInfo("Examenes generados");
        // Remove container with the form for generating exams from the view
        mainGenContainer.getChildren().remove(generateContainer);
        // TODO: Add table with the generated exams
        // Add buttons for downloading the newly generated exams
        mainGenContainer.getChildren().add(downloadContainer);
        // Reset the generate form fields
        tfGrupo.setText("");
        spCantidad.decrement(100);
    }

    /**
     * areFieldsInvalid
     *
     * Verifies that the user-entered values in the Generate form fields are valid.
     * @return True if the user-entered values are invalid, false otherwise.
     *
     */
    private boolean areFieldsInvalid() {
        // Validate that a group was entered
        if (tfGrupo.getText() == null || tfGrupo.getText().equals("")) {
            Alerts.displayError("Error", "Favor de ingresar un grupo");
            return true;
        }

        // Validate that the entered amount of exams is larger than 0
        if (spCantidad.getValue() == null ||
                Integer.parseInt(spCantidad.getValue().toString()) <= 0) {
            Alerts.displayError("Error", "Favor de ingresar una cantidad de examenes mayor a 0");
            return true;
        }
        return false;
    }

    /**
     * loadGenerateForm
     *
     * Loads the subjects from the QuestionBank instance in memory if there is at least one subject.
     * In case there are no subjects, prevent the exam generation by disabling the btnGenerate.
     *
     */
    public void loadGenerateForm(VBox mainGenContainer) {
        if (firstLoad) {
            firstLoad = false;
            inflateViews();
            // Display the generate exams form to the user
            mainGenContainer.getChildren().add(generateContainer);
        }
        //mainGenContainer.getChildren().clear();
        if (generateContainer != null) {
            // Checks if there is at least one subject in the QuestionBank
            if (mParentController.getQuestionBank().getSubjects().isEmpty()) {
                btnGenerate.setDisable(true);
            }
            else {
                btnGenerate.setDisable(false);
                // Load each subject name from the QuestionBank into the combo box
                mParentController.getQuestionBank()
                                 .getSubjects()
                                 .stream()
                                 .filter(s -> !cbTema.getItems().contains(s.getSubjectName()))
                                 .forEach(s -> {
                                     cbTema.getItems().add(s.getSubjectName());
                                 });

                // Set the preselected subject in the combo box as the first one
                cbTema.getSelectionModel().selectFirst();
            }
        }
    }


    /**
     * Randomly selects questions from the QuestionBank to generate a requested amount of exams.
     *
     * @param subject The subject of the exams to be generated
     * @param amount The amount of exams to be generated
     * @param group The group of the exams to be generated
     * @return examBank The set of generated exams
     */
    public ExamBank generateExams(Subject subject,
                                  int amount,
                                  String group){
        ArrayList<Question> questions = new ArrayList<>();
        Exam exam;
        ExamBank examBank = new ExamBank();

        // Generates amount exams
        for(int i = 0; i < amount; i++){
            // For each block in the subject, add a random question
            questions.addAll(subject.getBlocks()
                                     .stream()
                                     .map(b -> b.getQuestions()
                                                .get(ThreadLocalRandom.current()
                                                                      .nextInt(0,
                                                                               b.getQuestions()
                                                                                .size())))
                                     .collect(Collectors.toList()));
            exam = new Exam(subject.getSubjectName(), group, new ArrayList<>(questions));
            examBank.addExam(subject.getSubjectName(), exam);
            // Reuse the questions variable for another exam
            questions.clear();
        }
        return examBank;
    }

}
