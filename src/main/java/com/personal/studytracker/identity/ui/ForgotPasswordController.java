package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.DatabaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private TextField answerField;


    @FXML
    private void handleLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/identity/ui/login-view.fxml"));

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Study Tracker - Log in");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            alerts.show(Alert.AlertType.ERROR, usernameField.getScene().getWindow(), "Navigation Error", "Could not load the login page.");
        }
    }
}
