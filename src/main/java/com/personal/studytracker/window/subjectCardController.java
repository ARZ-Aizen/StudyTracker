package com.personal.studytracker.window;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.session;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class subjectCardController {

    //

    @FXML private Label cardSubjectName, cardSubjectCode;
    @FXML private Button btnEdit, btnDelete;

    //

    private Runnable refreshCallback;
    private String currentCode;

    //

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setData(String subject, String code) {
        this.currentCode = code;
        cardSubjectName.setText(subject);
        cardSubjectCode.setText(code);
        cardSubjectName.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        cardSubjectCode.setStyle("-fx-text-fill: #666666;");
    }

    @FXML private void courseEditButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/course-edit-view.fxml"));
            Parent popupRoot = loader.load();

            courseManage controller = loader.getController();
            controller.setFields(cardSubjectName.getText(), cardSubjectCode.getText());

            Parent ownerRoot = btnEdit.getScene().getRoot();
            Stage popup = transition.popupWithRoot(ownerRoot, popupRoot, "Edit Subject");

            if (popup != null) {
                popup.setOnHidden(e -> {
                    ownerRoot.setEffect(null);
                    if (refreshCallback != null) refreshCallback.run();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void deleteCourse() {

        boolean proceed = alerts.showConfirmation(btnDelete.getScene().getWindow(), "Delete Subject", "Do you want to delete this subject?");

        if (proceed) {
            String query = "DELETE FROM subject WHERE code = ? AND user_id = ?";

            try (Connection conn = databaseConnectionManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, currentCode);
                pstmt.setInt(2, session.getUserId());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0 || refreshCallback != null) {
                    refreshCallback.run();
                    alerts.show(AlertType.INFORMATION, null, "Deleted", "Subject removed successfully.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}