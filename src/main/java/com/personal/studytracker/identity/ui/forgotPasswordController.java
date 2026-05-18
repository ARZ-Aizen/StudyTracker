package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class forgotPasswordController {

    //

    @FXML private TextField usernameField;
    @FXML private Button findUsername;
    //

    @FXML private void handleLogin() {
        Parent root = usernameField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login", false);
    }

    @FXML private void handleUsername() {
        String username = usernameField.getText().trim();
        Window owner = usernameField.getScene().getWindow();

        if (username.isEmpty()) {
            alerts.show(Alert.AlertType.WARNING, owner, "Validation Error", "Please enter your username.");
            return;
        }

        String sql = "SELECT user_id, question FROM users WHERE username = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int foundUserId = rs.getInt("user_id");
                String securityQuestion = rs.getString("question");

                if (securityQuestion == null || securityQuestion.isEmpty()) {
                    alerts.show(Alert.AlertType.ERROR, owner, "Error", "This account does not have a security question set up.");
                    return;
                }

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/identity/ui/reset-password-view.fxml"));
                    Parent nextRoot = loader.load();

                    resetPasswordController nextController = loader.getController();
                    nextController.setSecurityData(foundUserId, securityQuestion);

                    Parent currentRoot = usernameField.getScene().getRoot();
                    transition.effects(currentRoot, nextRoot, "Study Tracker - Security Question", false);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                alerts.show(Alert.AlertType.ERROR, owner, "Not Found", "No account found with that username.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
