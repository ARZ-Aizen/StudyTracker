package com.personal.studytracker.window;

import com.personal.studytracker.config.DatabaseConnectionManager;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SubjectCardController {

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
        String query = "DELETE FROM subject WHERE code = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, currentCode);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0 || refreshCallback != null) {
                refreshCallback.run();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}