package com.personal.studytracker.window;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class courseManage {

    //

    @FXML private TextField nameField, codeField;
    @FXML private Button cancelButton, saveButton;

    //

    private String originalCode;

    //

    @FXML protected void addProcess() {
        String subjectName = nameField.getText();
        String subjectCode = codeField.getText();

        if (subjectName.isBlank() || subjectCode.isBlank()) {
            alerts.show(Alert.AlertType.ERROR, nameField.getScene().getWindow(), "Error", "Please fill in all fields.");
            return;
        }

        if (addSubject(subjectName, subjectCode)) {
            alerts.show(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(), "Success", "Updated successfully!");
            ((Stage) saveButton.getScene().getWindow()).close();
            }
        }


    public void setFields(String name, String code) {
        nameField.setText(name);
        codeField.setText(code);
        this.originalCode = code;
    }

    @FXML protected void editProcess() {
        String newName = nameField.getText();
        String newCode = codeField.getText();

        if (newName.isBlank() || newCode.isBlank()) {
            alerts.show(Alert.AlertType.ERROR, nameField.getScene().getWindow(), "Error", "Fields cannot be empty.");
            return;
        }

        if (updateSubject(newName, newCode, originalCode)) {
            alerts.show(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(), "Success", "Updated successfully!");
            ((Stage) saveButton.getScene().getWindow()).close();
        } else {
            alerts.show(Alert.AlertType.ERROR, saveButton.getScene().getWindow(), "Error", "Failed to update database.");
        }
    }

    private boolean addSubject(String subjectName, String subjectCode) {
        String sql = "INSERT INTO subject (subject, code, user_id) VALUES (?, ?, ?)";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subjectName);
            pstmt.setString(2, subjectCode);
            pstmt.setInt(3, session.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateSubject(String newName, String newCode, String oldCode) {
        String sql = "UPDATE subject SET subject = ?, code = ? WHERE code = ? AND user_id = ?" ;

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setString(2, newCode);
            pstmt.setString(3, oldCode);
            pstmt.setInt(4, session.getUserId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML private void cancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
