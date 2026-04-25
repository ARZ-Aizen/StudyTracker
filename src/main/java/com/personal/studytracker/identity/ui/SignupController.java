package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.DatabaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupController {

    @FXML
    private TextField createUsername;
    @FXML
    private PasswordField createPassword;
    @FXML
    private PasswordField confirmPassword;

    @FXML
    protected void signupProcess() {
        String username = createUsername.getText().trim();
        String password = createPassword.getText();
        String checkPassword = confirmPassword.getText();

        if (username.isBlank() || password.isBlank() || checkPassword.isBlank()) {
            alerts.show(AlertType.ERROR, createUsername.getScene().getWindow(), "Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(checkPassword)) {
            alerts.show(AlertType.ERROR, createUsername.getScene().getWindow(), "Error", "Password do not match");
            return;
        }

        if (signupCreation(username, password)) {
            alerts.show(AlertType.INFORMATION, createUsername.getScene().getWindow(), "Success", "Account Created Successfully");
            handleLogin();
        } else {
            alerts.show(AlertType.ERROR, createUsername.getScene().getWindow(), "Error", "Account Creation Failed");
        }
    }

    private boolean signupCreation(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleLogin() {
        try {
            Stage stage = (Stage) createUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/identity/ui/login-view.fxml"));

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Study Tracker - Log in");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            alerts.show(AlertType.ERROR, createUsername.getScene().getWindow(), "Navigation Error", "Could not load the login page.");
        }
    }
}


