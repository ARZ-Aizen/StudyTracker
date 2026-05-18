package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Window;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class newPasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private int recoveringUserId;

    public void setUserId(int id) {
        this.recoveringUserId = id;
    }

    @FXML
    private void handleResetPassword() {
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();
        Window owner = newPasswordField.getScene().getWindow();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            alerts.show(Alert.AlertType.WARNING, owner, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            alerts.show(Alert.AlertType.ERROR, owner, "Mismatch", "Passwords do not match. Please try again.");
            return;
        }

        if (newPass.length() < 4) {
            alerts.show(Alert.AlertType.WARNING, owner, "Weak Password", "Password must be at least 4 characters long.");
            return;
        }

        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPass);
            pstmt.setInt(2, this.recoveringUserId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                alerts.show(Alert.AlertType.INFORMATION, owner, "Success", "Your password has been reset successfully. You can now log in.");
                Parent root = newPasswordField.getScene().getRoot();
                transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login", false);
            } else {
                alerts.show(Alert.AlertType.ERROR, owner, "Error", "Failed to update password. User not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alerts.show(Alert.AlertType.ERROR, owner, "Database Error", "An error occurred while communicating with the database.");
        }
    }

    @FXML
    private void handleCancel() {
        Parent root = newPasswordField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login", false);
    }
}