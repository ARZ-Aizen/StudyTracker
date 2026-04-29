package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.DatabaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
            Parent root = usernameField.getScene().getRoot();
            transition.effects(root, "/com/personal/studytracker/dashboard/ui/dashboard-view.fxml", "Study Tracker - Home", true);
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
        Parent root = usernameField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/signup-view.fxml", "Study Tracker - Sign Up", false);
    }


    @FXML
    private void handleForgotPass() {
        Parent root = usernameField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/forgot-password-view.fxml", "Study Tracker - Forgot Password", false);
    }
}



