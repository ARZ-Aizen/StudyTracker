package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.DatabaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupController {

    @FXML private TextField createUsername;
    @FXML private PasswordField createPassword;
    @FXML private PasswordField confirmPassword;

    @FXML
    protected void signupProcess() {
        String username = createUsername.getText();
        String password = createPassword.getText();
        String checkPassword = confirmPassword.getText();

        if (username.isBlank() || password.isBlank() || checkPassword.isBlank()) {
            alerts.show(AlertType.ERROR, createUsername.getScene().getWindow(),"Error", "Please enter your both username and password");
        }

    }
}
