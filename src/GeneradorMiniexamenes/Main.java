package GeneradorMiniexamenes;

import GeneradorMiniexamenes.controllers.Import;
import GeneradorMiniexamenes.model.QuestionBank;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private QuestionBank mQuestionBank;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main" +
                                                                     ".fxml"));
        primaryStage.setTitle("Generate de Minexamenes");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public QuestionBank getQuestionBank() {
        return mQuestionBank;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
