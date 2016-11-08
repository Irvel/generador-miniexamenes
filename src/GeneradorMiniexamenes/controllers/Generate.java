package GeneradorMiniexamenes.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Randomly generates exams from the subjects and questions available in the
 * model.
 */
public class Generate {

    @FXML
    private Button btnGenerate;

    public Generate(){
        // Revisar que haya un banco de preguntas existente
        initializeOptions();
    }

    private void initializeOptions() {
        /*BancoDePreguntas.cargarBanco();
        if (BancoDePreguntas.getCantidadTemas() == 0) {
            // Desabilitar el boton de generar
            generarBtn.setDisable();
        }*/
    }

}
