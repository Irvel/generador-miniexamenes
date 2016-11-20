package GeneradorMiniexamenes.controllers;

import GeneradorMiniexamenes.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Randomly generates exams from the subjects and questions available in the
 * model.
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

    private HBox mGenerateContainer;
    private HBox mDownloadContainer;
    private boolean mFirstLoad;
    private String mLatexExams;
    private String mLastGeneratedSubject;

    private final String[] MAC_CONVERTER_PATHS = {"/Library/TeX/texbin/pdflatex", "/usr/texbin/pdflatex",
                                                  "/usr/texbin/pdftex", "/Library/TeX/texbin/pdftex",
                                                  "/Library/TeX/Root/bin/x86_64-darwin/pdflatex"};

    private final String[] WIN_CONVERTER_PATHS = {"C:\\Program Files\\MiKTeX " +
                                                          "2.8\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "2.9\\miktex\\bin\\x64\\pdflatex.exe",
                                                  "C:\\Program Files\\MiKTeX " +
                                                          "3.0\\miktex\\bin\\x64\\pdflatex.exe"};


    public Generate(MainController parentController) {
        // Keep a reference to the main controller to get important shared variables
        mParentController = parentController;
        mFirstLoad = true;
        mLatexExams = "";
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
            loader.setLocation(getClass().getResource("/fxml/GenExamsAction.fxml"));
            loader.setController(this);
            mGenerateContainer = loader.load();

            // Load the interface for downloading the Generated exams but do not show it yet
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/GenExamsDownload.fxml"));
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
        VBox mainGenContainer = (VBox) mGenerateContainer.getParent();
        // Remove container with the form for generating exams from the view
        mainGenContainer.getChildren().remove(mGenerateContainer);
        // TODO: Add table with the generated exams
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
        tfGrupo.setText("");
        spCantidad.decrement(100);
        if (mGenerateContainer != null) {
            // Checks if there is at least one subject in the QuestionBank
            if (mParentController.getQuestionBank().getSubjects().isEmpty()) {
                btnGenerate.setDisable(true);
                cbTema.setDisable(true);
            }
            else {
                btnGenerate.setDisable(false);
                cbTema.setDisable(false);
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
        // Generate the LaTeX document of the exams and store in the member variable
        mLatexExams = ExamTemplate.makeLatexExams(examBank.getExams(subject.getSubjectName()));
        // Store the last generated subject for the filename when downloading the generated exams
        mLastGeneratedSubject = subject.getSubjectName();
        return examBank;
    }

    /**
     * backGenAction
     *
     * The user clicked the back button after having generated exams
     * @param actionEvent
     */
    public void backGenAction(ActionEvent actionEvent) {
        // Get a reference to the parent container to be able to add the Generate form to it
        VBox parentContainer = (VBox) mDownloadContainer.getParent();

        // Clean the container to re-initialize the Generate exams form view
        parentContainer.getChildren().clear();

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
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        // Set extension filter
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TEX files (*.tex)", "*.tex"));

        fileChooser.setInitialFileName("Examenes " + mLastGeneratedSubject + ".tex");

        // Show the "save exams in LaTeX dialog"
        File latexFile = fileChooser.showSaveDialog(currentStage);

        // The user canceled the save dialog, don't do anything
        if (latexFile == null) {
            return;
        }

        try {
            Writer latexOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
                                                                                (latexFile),
                                                            "UTF-8"));
            latexOut.write(mLatexExams);
            latexOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Alerts.displayError("Error", "No fue posible escribir en la ruta especificada.");
        }

    }

    /**
     * downloadPdfAction
     *
     * The user clicked the "download all exams in PDF" button. Prompt a file save dialog and
     * save the last generated exams in the PDF format by first saving the LaTeX file and then
     * converting it to PDF with the external pdflatex tool.
     * @param actionEvent
     */
    public void downloadPdfAction(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        // Set extension filter
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        fileChooser.setInitialFileName("Examenes " + mLastGeneratedSubject + ".pdf");

        // Show the "save exams in Pdf dialog"
        File selectedFile = fileChooser.showSaveDialog(currentStage);

        // The user canceled the save dialog, don't do anything
        if (selectedFile == null) {
            return;
        }
        String targetDirectoryPath = selectedFile.getParent();
        String tempDirectoryPath = targetDirectoryPath +  File.separator + "generadorMiniTemp";
        // Delete previous temp dir if it still exists
        File tempDir  = new File(tempDirectoryPath);
        if (tempDir.exists()) {
            removeDirectory(tempDir);
        }
        //TODO: Refactor this to use the other method that does almost the same thing as this
        boolean couldCreateDirectory = (new File(tempDirectoryPath)).mkdir();
        if (couldCreateDirectory) {
            Writer latexOut = null;
            try {
                File latexTemp = new File(tempDirectoryPath + File.separator + "latexTemp.tex");
                latexOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(latexTemp),
                                                                     "UTF-8"));
                try {
                    latexOut.write(mLatexExams);
                    latexOut.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Alerts.displayError("Error", "No fue posible escribir en la ruta especificada.");
                }
                convertToPdf(tempDirectoryPath,
                             tempDirectoryPath + File.separator + "latexTemp.tex",
                             selectedFile.getName());
            }
            catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
                Alerts.displayError("Error", "No fue posible escribir en la ruta especificada.");
            }
        }
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
                                                   "-synctex=1",
                                                   "-interaction=nonstopmode",
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
            Alerts.displayError("Error", "No fue posible exportar los examenes en formato PDF. " +
                    "Verifique que tenga una instalación funcional de LaTeX");
        }
    }

    private void removeDirectory(File tempDir) {
        for (File c : tempDir.listFiles()) {
            c.delete();
        }
        tempDir.delete();
    }

    private ArrayList<String> getConverterPaths(String systemName) {
        ArrayList<String> converterPaths = new ArrayList<String>();
        if (systemName.toLowerCase().startsWith("mac")) {
            converterPaths.add(findConverter());
            converterPaths.addAll(Arrays.asList(MAC_CONVERTER_PATHS));
        }
        else if (systemName.toLowerCase().startsWith("win")) {
            converterPaths.addAll(Arrays.asList(WIN_CONVERTER_PATHS));
            converterPaths.addAll(getMikTexPaths());
        }
        else {
            // The OS is linux or something else
            converterPaths.add(findConverter());
        }
        return converterPaths;
    }

    /**
     * findConverter
     *
     * Attempt to find the location of pdflatex with the "which" command in UNIX based operating
     * systems. This will fail if the path is not configured properly in the target system.
     * @return The absolute path to the pdflatex executable
     *
     */
    private String findConverter() {
        // Calling just "which" doesn't work for some reason. Using the whole path kind of
        // defeats the whole purpose of trying to use which to find pdflatex.
        ProcessBuilder builder = new ProcessBuilder("/usr/bin/which pdflatex");
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String path = reader.readLine();
            if (path != null) {
                return path;
            }
        }
        catch (IOException e) {
            System.out.println("Trying to run \"which\" failed. Falling back to hardcoded " +
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
        Alerts.displayError("Error: pdflatex no encontrado", "El programa pdflatex no fue " +
                "encontrado. Favor de instalar pdflatex o pdftex para habilitar la exportación " +
                "directa a PDF.");
        return null;
    }

    // TODO: Remove this ugly hack
    private ArrayList<String> getMikTexPaths() {
        ArrayList<String> paths = new ArrayList<>();
        // Poor attempt at future-proofing
        for (int i = 1; i < 18; i++) {
            paths.add("\"C:\\\\Program Files\\\\MikTeX 2." + Integer.toString(i) +
                              "\"\\\\pdflatex.exe\",");
            paths.add("\"C:\\\\Program Files\\\\MikTeX 2." + Integer.toString(i) +
                              "\"\\\\pdftex.exe\",");
            paths.add("\"C:\\\\Program Files (x86)\\\\MikTeX 2." + Integer.toString(i) +
                              "\"\\\\pdflatex.exe\",");
            paths.add("\"C:\\\\Program Files (x86)\\\\MikTeX 2." + Integer.toString(i) +
                              "\"\\\\pdftex.exe\",");
        }
        for (int i = 0; i < 10; i++) {
            paths.add("\"C:\\\\Program Files\\\\MikTeX 3." + Integer.toString(i) +
                              "\"\\\\pdflatex.exe\",");
            paths.add("\"C:\\\\Program Files\\\\MikTeX 3." + Integer.toString(i) +
                              "\"\\\\pdftex.exe\",");
            paths.add("\"C:\\\\Program Files (x86)\\\\MikTeX 3." + Integer.toString(i) +
                              "\"\\\\pdflatex.exe\",");
            paths.add("\"C:\\\\Program Files (x86)\\\\MikTeX 3." + Integer.toString(i) +
                              "\"\\\\pdftex.exe\",");
        }
        return paths;
    }
}
