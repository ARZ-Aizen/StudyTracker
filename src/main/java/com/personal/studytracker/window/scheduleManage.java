package com.personal.studytracker.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class scheduleManage {

    @FXML private Button cancelButton;

    @FXML
    private void cancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
