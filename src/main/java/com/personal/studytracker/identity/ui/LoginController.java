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
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

        @FXML private TextField usernameField;
        @FXML private PasswordField passwordField;

    @FXML
    protected void loginProcess () {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            alerts.show(AlertType.ERROR, usernameField.getScene().getWindow(), "Error", "Please enter your both username and password");
            return;
        }

        if (loginValidation(username, password)) {
            alerts.show(AlertType.INFORMATION, usernameField.getScene().getWindow(),"Success", "Login Successfully!");
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/dashboard/ui/dashboard-view.fxml"));

                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle("Study Tracker");
                stage.setResizable(true);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                alerts.show(AlertType.ERROR, usernameField.getScene().getWindow(), "Navigation Error", "Could not load the home page.");
            }
        } else {
            alerts.show( AlertType.ERROR, usernameField.getScene().getWindow(),"Login Failed", "Invalid username or password");
        }
    }

    private boolean loginValidation (String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleSignup() {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/identity/ui/signup-view.fxml"));

                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle("Study Tracker - Sign Up");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                alerts.show(AlertType.ERROR, usernameField.getScene().getWindow(), "Navigation Error", "Could not load the signup page.");
            }
        }

    @FXML
    private void handleForgotPass() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/identity/ui/forgot-password-view.fxml"));

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Study Tracker - Forgot Password");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            alerts.show(AlertType.ERROR, usernameField.getScene().getWindow(), "Navigation Error", "Could not load the forgot password page.");
        }
    }
}

