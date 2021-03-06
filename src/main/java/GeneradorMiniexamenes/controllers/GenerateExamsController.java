package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static GeneradorMiniexamenes.controllers.AlertMaker.displayInfo;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Randomly generates exams from the subjects and questions available in the
 * model.
 */
public class GenerateExamsController {
    // Keep a reference to the Model of the application
    private QuestionBank mQuestionBank;
    private ExamBank mExamBank;
    private Options mOptions;

    @FXML private JFXComboBox<String> comboBoxSubject;
    @FXML private SpinnerAutoCommit spinnerAmount;
    @FXML private JFXButton buttonGenerate;
    @FXML private JFXComboBox<String> cbGenFieldGroup;
    @FXML private AnchorPane parentGenContainer;
    @FXML private JFXButton buttonDownLatexGen;
    @FXML private JFXButton buttonDownPdfGen;

    private HBox mGenerateContainer;
    private VBox mDownloadContainer;
    private ExamListView mGenExamsView;
    private boolean mFirstLoad;
    
    public GenerateExamsController(QuestionBank questionBank, ExamBank examBank, Options options) {
        mQuestionBank = questionBank;
        mExamBank = examBank;
        mFirstLoad = true;
        mOptions = options;
    }

    /**
     * inflateViews
     *
     * Construct objects from the fxml view definitions and store them as member variables.
     */
    private void inflateViews() {
        try {
            // Delete any existing inflated views
            mGenerateContainer = null;
            mDownloadContainer = null;

            // Load the form for generating exams but do not show it yet
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GenerateExamsTab/GenAction.fxml"));
            loader.setController(this);
            mGenerateContainer = loader.load();

            // Load the interface for downloading the Generated exams but do not show it yet
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GenerateExamsTab/GenDownload.fxml"));
            loader.setController(this);
            mDownloadContainer = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generateAction
     *
     * The user clicked the generate button
     * @param actionEvent
     */
    public void generateAction(ActionEvent actionEvent) {
        if (areFieldsInvalid()) {
            return;
        }

        // Get the selected subject object from the comboBoxSubject ComboBox
        Subject subject = mQuestionBank.getSubjectByName(comboBoxSubject.getValue());
        int examQuantity = Integer.parseInt(spinnerAmount.getValue().toString());
        Group generatedExams = generateExams(subject,
                                             examQuantity,
                                             cbGenFieldGroup.getValue());

        // Add the newly generated exams to the list of generated exams for this subject
        mExamBank.addGroup(subject.getSubjectName(), generatedExams);
        displayGeneratedExams(generatedExams);
    }

    /**
     * displayGeneratedExams
     *
     * Display the generated exams in another view and show the options for downloading the
     * generated exams.
     *
     * @param generatedExams The set of generated exams that is to be shown to the user
     *
     */
    private void displayGeneratedExams(Group generatedExams) {
        VBox mainGenContainer = (VBox) mGenerateContainer.getParent();
        // Remove container with the form for generating exams from the view
        mainGenContainer.getChildren().remove(mGenerateContainer);
        EventHandler<MouseEvent> mouseVersion = event -> {
            if (mGenExamsView == null || mGenExamsView.getSelectedExams() == null) {
                return;
            }
            int selectedAmount = mGenExamsView.getSelectedExams().size();
            if (selectedAmount == 0) {
                return;
            }
            // Give visual feedback with on the selection with the buttons themselves
            buttonDownLatexGen.setDisable(false);
            buttonDownPdfGen.setDisable(false);

            if (selectedAmount == 1) {
                buttonDownLatexGen.setText("Descargar " + selectedAmount + " examen en formato " +
                                                   "LaTeX");
                buttonDownPdfGen.setText("Descargar " + selectedAmount + " examen en formato " +
                                                 "PDF");
            }
            else {
                buttonDownLatexGen.setText("Descargar " + selectedAmount + " exámenes en formato " +
                                                   "LaTeX");
                buttonDownPdfGen.setText("Descargar " + selectedAmount + " exámenes en formato " +
                                                 "PDF");
            }
        };
        ChangeListener examListListener = (ov, t, t1) -> {
            if (mGenExamsView == null || mGenExamsView.getSelectedExams() == null) {
                return;
            }
            int selectedAmount = mGenExamsView.getSelectedExams().size();
            // Give visual feedback with on the selection with the buttons themselves
            buttonDownLatexGen.setDisable(false);
            buttonDownPdfGen.setDisable(false);

            if (selectedAmount == 1) {
                buttonDownLatexGen.setText("Descargar " + selectedAmount + " examen en formato " +
                                                   "LaTeX");
                buttonDownPdfGen.setText("Descargar " + selectedAmount + " examen en formato " +
                                                   "PDF");
            }
            else {
                buttonDownLatexGen.setText("Descargar " + selectedAmount + " exámenes en formato " +
                                                   "LaTeX");
                buttonDownPdfGen.setText("Descargar " + selectedAmount + " exámenes en formato " +
                                                   "PDF");
            }
        };

        mGenExamsView = new ExamListView(parentGenContainer, examListListener, mouseVersion);
        mGenExamsView.refreshListView(generatedExams);
        // Add buttons for downloading the newly generated exams
        mainGenContainer.getChildren().add(mDownloadContainer);
    }

    /**
     * resetFormFields
     *
     * Reset the generate exams form fields with questions from the QuestionBank. In case there
     * are no questions in the QuestionBank, disable the generate button.
     *
     */
    private void resetFormFields() {
        // Add the stored groups to the combo box
        cbGenFieldGroup.getItems().clear();
        if (mOptions.getGroupNames().size() <= 0) {
            displayInfo("Para poder generar exámenes, es necesario primero agregar un grupo en " +
                                "la pestaña de \"Opciones\".");
            buttonGenerate.setDisable(true);
            comboBoxSubject.setDisable(true);
            cbGenFieldGroup.setDisable(true);
            spinnerAmount.setDisable(true);
            return;
        }
        cbGenFieldGroup.getItems().addAll(mOptions.getGroupNames());
        cbGenFieldGroup.getSelectionModel().selectFirst();
        spinnerAmount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 40));
        if (mGenerateContainer != null) {
            // Checks if there is at least one subject in the QuestionBank
            if (mQuestionBank.getSubjects().isEmpty()) {
                buttonGenerate.setDisable(true);
                comboBoxSubject.setDisable(true);
                cbGenFieldGroup.setDisable(true);
                spinnerAmount.setDisable(true);
                displayInfo("Para poder generar exámenes, es necesario primero agregar por lo " +
                                    "menos un tema al banco de preguntas en la pestaña de " +
                                    "\"Importar / Exportar\"");
            }
            else {
                buttonGenerate.setDisable(false);
                comboBoxSubject.setDisable(false);
                cbGenFieldGroup.setDisable(false);
                spinnerAmount.setDisable(false);
                // Load each subject name from the QuestionBank into the combo box
                mQuestionBank.getSubjects()
                             .stream()
                             .filter(s -> !comboBoxSubject.getItems().contains(s.getSubjectName()))
                             .forEach(s -> comboBoxSubject.getItems().add(s.getSubjectName()));

                // Set the preselected subject in the combo box as the first one
                comboBoxSubject.getSelectionModel().selectFirst();
            }
        }
    }

    /**
     * areFieldsInvalid
     *
     * Verifies that the user-entered values in the GenerateExamsController form fields are valid.
     * @return True if the user-entered values are invalid, false otherwise.
     *
     */
    private boolean areFieldsInvalid() {
        // Validate that the entered amount of exams is larger than 0
        if (spinnerAmount.getValue() == null ||
                Integer.parseInt(spinnerAmount.getValue().toString()) <= 0) {
            AlertMaker.displayError("Error", "Favor de ingresar una cantidad de examenes mayor a 0");
            return true;
        }
        return false;
    }

    /**
     * loadGenerateForm
     *
     * Loads the subjects from the QuestionBank instance in memory if there is at least one subject.
     * In case there are no subjects, prevent the exam generation by disabling the buttonGenerate.
     * @param mainContainer A reference to the main container to which the GenerateForm will
     *                         be added in
     *
     */
    public void loadGenerateForm(VBox mainContainer) {
        // If this is the first time loading the view
        if (mFirstLoad) {
            mFirstLoad = false;
            inflateViews();
            // Display the generate exams form view to the user
            mainContainer.getChildren().add(mGenerateContainer);
        }
        // Update the form options from the loaded QuestionBank
        resetFormFields();
    }


    /**
     * Randomly selects questions from the QuestionBank to generate a requested amount of exams.
     *
     * @param subject The subject of the exams to be generated
     * @param amount The amount of exams to be generated
     * @param groupName The group of the exams to be generated
     * @return examBank The set of generated exams
     */
    public Group generateExams(Subject subject, int amount, String groupName) {
        ArrayList<Question> questions = new ArrayList<>();
        ArrayList<Exam> generatedExams = new ArrayList<>();
        int startingExamNumber = mExamBank.getHighestExamNumber(subject.getSubjectName(), groupName);

        // Generates the user selected amount of exams
        for (int i = 1; i <= amount; i++) {
            // For each block in the subject, add a random question
            questions.addAll(subject.getBlocks()
                                     .stream()
                                     .map(b -> b.getQuestions()
                                                .get(ThreadLocalRandom.current()
                                                                      .nextInt(0,
                                                                               b.getQuestions()
                                                                                .size()))).collect(Collectors.toList()));
            // The exam number is the highest exam number in the group plus the sequence
            // number in the generated exams list
            Exam exam = new Exam(subject.getSubjectName(),
                            groupName,
                            new ArrayList<>(questions), startingExamNumber + i);
            generatedExams.add(exam);
            // Reuse the questions variable for another exam
            questions.clear();
        }

        return new Group(groupName, generatedExams);
    }

    /**
     * backGenAction
     *
     * The user clicked the back button after having generated exams
     * @param actionEvent
     */
    public void backGenAction(ActionEvent actionEvent) {
        // Get a reference to the parent container to be able to add the GenerateExamsController form to it
        VBox parentContainer = (VBox) mDownloadContainer.getParent();

        // Clean the container to re-initialize the GenerateExamsController exams form view
        parentContainer.getChildren().clear();
        mGenExamsView = null;

        // Set this to true so that loadGenerateForm reloads the views from the fxml's
        mFirstLoad = true;
        loadGenerateForm(parentContainer);
    }

    /**
     * downloadLatexAction
     *
     * The user clicked the "download all exams in LaTeX" button. Prompt a file save dialog and
     * save the last generated exams in the LaTeX format.
     * @param actionEvent
     */
    public void downloadLatexAction(ActionEvent actionEvent) {
        ArrayList<Exam> generatedExams = mGenExamsView.getSelectedExams();
        if (generatedExams == null){
            return;
        }
        downloadLatexExams(actionEvent, generatedExams);
    }

    /**
     * downloadPdfAction
     *
     * The user clicked the "download all exams in PDF" button. Gather the files for the exams to
     * be downloaded an call the method to do so.
     * @param actionEvent
     */
    public void downloadPdfAction(ActionEvent actionEvent) {
        ArrayList<Exam> generatedExams = mGenExamsView.getSelectedExams();
        if (generatedExams == null){
            return;
        }
        downloadPdfExams(actionEvent, mGenExamsView.getSelectedExams());
    }

    /**
     * downloadLatexExams
     *
     * Prompt a file save dialog and save the received exams in a LaTeX file.
     *
     * @param actionEvent The actionEven in which the request for downloading the exams occured
     * @param exams The list of exams to be downloaded
     */
    public void downloadLatexExams(ActionEvent actionEvent, ArrayList<Exam> exams) {
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        final String fileName = makeFilename(exams.get(0).getSubject(),
                                             exams.get(0).getGroup(),
                                             exams.size());
        String latexExams = ExamTemplate.makeLatexExams(mOptions.getClassTitle(), exams);
        File latexFile = promptFileChooser(currentStage,
                                           fileName + ".tex",
                                           new FileChooser.ExtensionFilter("TEX files (*.tex)", "*.tex"));

        // The user canceled the save dialog, don't do anything
        if (latexFile == null) {
            return;
        }

        saveLatexToFile(latexExams, latexFile);
    }

    /**
     * downloadPdfExams
     *
     * Prompt a file save dialog and save the received exams in PDF to that location. To do this
     * it first creates a temporary LaTeX file and then uses the external tool pdflatex to
     * convert LaTeX to PDF.
     *
     * @param actionEvent The actionEven in which the request for downloading the exams occurred
     * @param exams The list of exams to be downloaded
     */
    public void downloadPdfExams(ActionEvent actionEvent, ArrayList<Exam> exams) {
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        final String fileName = makeFilename(exams.get(0).getSubject(),
                                             exams.get(0).getGroup(),
                                             exams.size());
        String latexExams = ExamTemplate.makeLatexExams(mOptions.getClassTitle(), exams);
        // Show the "save exams in Pdf dialog"
        File selectedFile = promptFileChooser(currentStage,
                                              fileName + ".pdf",
                                              new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        // The user canceled the save dialog, don't do anything
        if (selectedFile == null) {
            return;
        }
        String targetDirectoryPath = selectedFile.getParent();
        String tempDirectoryPath = targetDirectoryPath +  File.separator + ".generadorMiniTemp";
        boolean couldCreateDirectory = tempDirectorySetup(tempDirectoryPath);

        if (couldCreateDirectory) {
            File latexTemp = new File(tempDirectoryPath + File.separator + "latexTemp.tex");
            saveLatexToFile(latexExams, latexTemp);

            // Convert the temporal LaTeX file to PDF
            convertToPdf(tempDirectoryPath,
                         tempDirectoryPath + File.separator + "latexTemp.tex",
                         selectedFile.getName());
        }
        else {
            AlertMaker.displayError("Error", "No fue posible escribir en la ruta especificada.");
        }
    }

    /**
     * promptFileChooser
     *
     * Generic method to prompt a file save dialog in the given stage.
     * @return The file chosen by the user or null if the dialog was canceled
     */
    private File promptFileChooser(Stage stage,
                                   String initialFilename,
                                   FileChooser.ExtensionFilter extensions){
        FileChooser fileChooser = new FileChooser();
        // Set extension filter
        fileChooser.getExtensionFilters().add(extensions);

        fileChooser.setInitialFileName(initialFilename);
        // Show the dialog and capture the file if selected
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * saveLatexToFile
     *
     * Saves a given LaTeX string to a given file location.
     * @param latex The LaTeX string to save
     * @param latexFile The file in which the LaTeX string will be saved
     */
    private void saveLatexToFile(String latex, File latexFile) {
        try {
            Writer latexOut = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(latexFile), "UTF-8"));
            latexOut.write(latex);
            latexOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            AlertMaker.displayError("Error", "No fue posible escribir en la ruta especificada.");
        }
    }

    /**
     * makeFilename
     *
     * Create a filename from a subject and group.
     * @param subject The subject to create the filename from
     * @param group The group to create the filename from
     */
    private String makeFilename(String subject, String group, int quantity) {
        if (quantity == 1) {
            return "Examen " + subject + " - Grupo " + group;
        }
        return "Examenes " + subject + " - Grupo " + group;
    }

    /**
     * tempDirectorySetup
     * Tries to create a temporary directory in tempDirectoryPath.
     * Assumes the received path points to a hidden filename for the directory.
     * @param tempDirectoryPath The path to which the directory will be attempted to be created
     * @return True if it was able to create the directory in tempDirectoryPath
     */
    boolean tempDirectorySetup(String tempDirectoryPath) {
        // Delete previous temp dir if it still exists
        File tempDir  = new File(tempDirectoryPath);
        if (tempDir.exists()) {
            removeDirectory(tempDir);
        }

        // Create a temporal directory to store the intermediary LaTeX file
        boolean couldCreateDirectory = (tempDir).mkdir();
        // Set the temporal directory as hidden in windows
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            try {
                Files.setAttribute(tempDir.toPath(), "dos:hidden", true);
            }
            catch (IOException e) {
                e.printStackTrace();
                couldCreateDirectory = false;
            }
        }
        return couldCreateDirectory;
    }

    private void convertToPdf(String tempDirPath, String latexFilePath, String pdfName) {
        // Try to get a list of possible paths for the pdflatex converter based on the host OS name
        ArrayList<String> converterPaths = getConverterPaths(System.getProperty("os.name"));
        Process converter;
        try {
            // Check which of the converter paths are actually the pdflatex executable
            final String converterPath = getPdflatexPath(converterPaths);
            if (converterPath == null) {
                return;
            }
            File tempDir = new File(tempDirPath);
            ProcessBuilder pb = new ProcessBuilder(converterPath,
                                                   "-interaction=batchmode",
                                                   latexFilePath);
            pb.directory(tempDir);
            converter = pb.start();
            converter.waitFor();
            Files.move(new File(tempDirPath + File.separator + "latexTemp.pdf").toPath(),
                       new File(tempDirPath + File.separator + ".."+ File.separator + pdfName)
                               .toPath(), REPLACE_EXISTING);
            // Cleanup temporal files
            removeDirectory(tempDir);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AlertMaker.displayError("Error", "No fue posible exportar los examenes en formato PDF. " +
                    "Verifique que tenga una instalación funcional de LaTeX");
        }
    }

    /**
     * removeDirectory
     *
     * Delete all the files inside the directory recursively and then delete the directory itself
     * @param dirToDelete the directory to be deleted
     */
    void removeDirectory(File dirToDelete) {
        if (dirToDelete != null && dirToDelete.exists() && dirToDelete.isDirectory()) {
            for (File f : dirToDelete.listFiles()) {
                if (f.isDirectory()) {
                    removeDirectory(f);
                }
                else {
                    f.delete();
                }
            }
            dirToDelete.delete();
        }
    }

    private final String[] MAC_CONVERTER_PATHS = {"/Library/TeX/texbin/pdflatex", "/usr/texbin/pdflatex",
                                                  "/Library/TeX/Root/bin/x86_64-darwin/pdflatex",
                                                  "/Library/TeX/Root/bin/x86-darwin/pdflatex"};

    private final String[] WIN_CONVERTER_PATHS = {"C:\\Program Files\\MiKTeX " +
                                                          "2.8\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "2.8\\miktex\\bin\\pdflatex.exe",
                                                  "C:\\Program Files (x86)\\MiKTeX " +
                                                          "2.8\\miktex\\bin\\pdflatex.exe",
                                                  "C:\\Program Files (x86)\\MiKTeX " +
                                                          "2.8\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "2.9\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "2.9\\miktex\\bin\\pdflatex.exe",
                                                  "C:\\Program Files (x86)\\MiKTeX " +
                                                          "2.9\\miktex\\bin\\pdflatex.exe",
                                                  "C:\\Program Files (x86)\\MiKTeX " +
                                                          "2.9\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "3.0\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "3.0\\miktex\\bin\\pdflatex.exe",
                                                  "C:\\Program Files (x86)\\MiKTeX " +
                                                          "3.0\\miktex\\bin\\pdflatex.exe",
                                                  "C:\\Program Files (x86)\\MiKTeX " +
                                                          "3.0\\miktex\\bin\\x64\\pdflatex.exe"};

    private ArrayList<String> getConverterPaths(String systemName) {
        ArrayList<String> converterPaths = new ArrayList<>();
        // Add the naive pdflatex command. This should work all the time but for some reason it
        // occasionally doesn't.
        converterPaths.add("pdflatex");
        converterPaths.add(findConverter(systemName.toLowerCase()));
        if (systemName.toLowerCase().startsWith("mac")) {
            converterPaths.addAll(Arrays.asList(MAC_CONVERTER_PATHS));
        }
        else if (systemName.toLowerCase().startsWith("win")) {
            converterPaths.addAll(Arrays.asList(WIN_CONVERTER_PATHS));
        }
        return converterPaths;
    }

    /**
     * findConverter
     *
     * Attempt to find the location of pdflatex with the "which" command in UNIX based operating
     * systems or "Where" in windows. This will fail if the path is not configured properly in the
     * target system.
     * @return The absolute path to the pdflatex executable
     *
     */
    private String findConverter(String osName) {
        String command;
        if (osName.startsWith("win")) {
            command = "where pdflatex";
        }
        else {
            command = "which pdflatex";
        }
        try {
            Process process = Runtime.getRuntime().exec(command);
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String path = reader.readLine();
            if (path != null) {
                return path;
            }
        }
        catch (IOException e) {
            System.out.println("Trying to run" + command + "failed. Falling back to hardcoded " +
                                       "paths...");
        }
        return "";
    }

    /**
     * getPdflatexPath
     *
     * Test if any of the provided paths is the actual converter executable. If none is a valid
     * path, then return null.
     * @return The absolute path to an existing pdflatex executable
     *
     */
    private String getPdflatexPath(ArrayList<String> paths) {
        for (String currentPath : paths) {
            File converterPath = new File(currentPath);
            if (converterPath.exists()) {
                return currentPath;
            }
        }
        AlertMaker.displayError("Error: pdflatex no encontrado", "El programa pdflatex no fue " +
                "encontrado. Favor de instalar pdflatex para habilitar la exportación " +
                "directa a PDF.");
        return null;
    }
}
