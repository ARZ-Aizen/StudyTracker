package com.personal.studytracker.window;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.session;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.*;

public class scheduleCard {

    @FXML private Label cardScheduleName, cardSchedule;
    @FXML private Button btnEdit, btnDelete;

    private int currentScheduleId;
    private Runnable refreshCallback;

    public void setData(int id, String name, String day, String start, String end, Runnable refresh) {
        this.currentScheduleId = id;
        this.refreshCallback = refresh;
        cardScheduleName.setText(name);
        cardSchedule.setText(String.format("%s (%s - %s)", day, start, end));
        cardScheduleName.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        cardSchedule.setStyle("-fx-text-fill: #666666;");
    }

    @FXML private void scheduleEditButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/schedule-edit-view.fxml"));
            Parent root = loader.load();
            scheduleManage controller = loader.getController();

            String fullStr = cardSchedule.getText();
            String dayPart = fullStr.split(" ")[0];
            String times = fullStr.substring(fullStr.indexOf("(") + 1, fullStr.indexOf(")"));
            String startPart = times.split(" - ")[0];
            String endPart = times.split(" - ")[1];

            controller.setFields(currentScheduleId, cardScheduleName.getText(), dayPart, startPart, endPart);

            Parent ownerRoot = btnEdit.getScene().getRoot();
            Stage popup = transition.popupWithRoot(btnEdit.getScene().getRoot(), root, "Edit Schedule");


            if (popup != null) {
                popup.setOnHidden(e -> {
                    ownerRoot.setEffect(null);
                    if (refreshCallback != null) refreshCallback.run();
                });
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void deleteSchedule() {
        if (alerts.showConfirmation(btnDelete.getScene().getWindow(), "Delete", "Remove this schedule?")) {
            try (Connection conn = databaseConnectionManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM schedule WHERE schedule_id = ? AND user_id = ?")) {
                ps.setInt(1, currentScheduleId);
                ps.setInt(2, session.getUserId());
                if (ps.executeUpdate() > 0) {
                    if (refreshCallback != null) refreshCallback.run();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}