package GeneradorMiniexamenes.controllers;

import javafx.event.ActionEvent;

/**
 * Created by Elias Mera on 11/7/2016.
 */
public class MainController {
    private Import mImport;
    private Export mExport;
    private Generate mGenerate;

    public MainController() {
        this.mImport = new Import();
        this.mExport = new Export();
        this.mGenerate = new Generate();
    }

    public void importAction(ActionEvent actionEvent) {
        mImport.onClick(actionEvent);
    }
}
