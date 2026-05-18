package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class resetPasswordController {

    @FXML private Label securityQuestionLabel;
    @FXML private TextField answerField;
    private int recoveringUserId;


    public void setSecurityData(int userId, String question) {
        this.recoveringUserId = userId;
        this.securityQuestionLabel.setText(question);
    }

    @FXML private void handleAnswer() {
        String answer = answerField.getText();
        Window owner = answerField.getScene().getWindow();

        if (answer.isEmpty()) {
            alerts.show(Alert.AlertType.WARNING, owner, "Error", "Please fill the field.");
            return;
        }

        String sql = "SELECT user_id FROM users WHERE user_id = ? AND answers = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, this.recoveringUserId);
            pstmt.setString(2, answer);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/identity/ui/new-password-view.fxml"));
                    Parent nextRoot = loader.load();
                    newPasswordController nextController = loader.getController();
                    nextController.setUserId(this.recoveringUserId);

                    Parent currentRoot = answerField.getScene().getRoot();
                    transition.effects(currentRoot, nextRoot, "Study Tracker - Create New Password", false);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                alerts.show(Alert.AlertType.ERROR, owner, "Incorrect", "That answer is incorrect. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML private void handleCancel() {
        Parent root = answerField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login", false);
    }

}
