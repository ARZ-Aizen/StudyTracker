package com.personal.studytracker.window;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class scheduleManage {

    //

    @FXML private Button saveButton, cancelButton;
    @FXML private ComboBox<String> subject, day, startTime, endTime;

    //

    @FXML
    public void initialize() {

        int loggedInUserId = session.getUserId();
            List<String> dbSubjects = getSubjectList(loggedInUserId);
            subject.getItems().addAll(dbSubjects);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        List<String> timeSlots = generateTimeIntervals(5, 21, 5);

        startTime.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (endTime.getValue() != null && newVal != null) {
                if (!isTimeRangeValid(newVal, endTime.getValue())) {
                    endTime.setValue(null);
                }
            }
        });

            startTime.getItems().addAll(timeSlots);
            endTime.getItems().addAll(timeSlots);
            day.getItems().addAll(days);
    }

    private List<String> generateTimeIntervals(int startHour, int endHour, int interval) {
        List<String> times = new ArrayList<>();

        for (int h = startHour; h <= endHour; h++) {
            for (int m = 0; m < 60; m += interval) {
                if (h == endHour && m > 0) break;

                String period = (h < 12) ? "AM" : "PM";
                int displayHour = h;
                if (h > 12) displayHour = h - 12;
                if (h == 0) displayHour = 12;

                String time = String.format("%d:%02d %s", displayHour, m, period);
                times.add(time);
            }
        }
        return times;
    }

    private List<String> getSubjectList(int currentUserId) {
        List<String> subjects = new ArrayList<>();
        String query = "SELECT subject FROM subject WHERE user_id = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjects.add(rs.getString("subject"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subjects;
    }

    private boolean isTimeRangeValid(String startStr, String endStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        try {
            LocalTime start = LocalTime.parse(startStr, formatter);
            LocalTime end = LocalTime.parse(endStr, formatter);

            return start.isBefore(end);
        } catch (Exception e) {
            return false;
        }
    }

    //

    @FXML
    protected void addProcess() {
        String startValue = startTime.getValue();
        String endValue = endTime.getValue();
        String selectedSub = subject.getValue();
        String selectedDay = day.getValue();

        if (selectedSub == null || selectedDay == null || startValue == null || endValue == null) {
            alerts.show(Alert.AlertType.ERROR, subject.getScene().getWindow(),
                    "Error", "Please fill in all fields.");
            return;
        }

        if (!isTimeRangeValid(startValue, endValue)) {
            alerts.show(Alert.AlertType.ERROR, startTime.getScene().getWindow(),
                    "Invalid Time Range",
                    "End time (" + endValue + ") cannot be earlier than or equal to start time (" + startValue + ").");
            return;
        }

        int subjectId = getSubjectIdByName(selectedSub);
        if (subjectId != -1) {
            if (addSchedule(subjectId, selectedDay, startValue, endValue)) {
                alerts.show(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(),
                        "Success", "Schedule saved successfully!");
                ((Stage) saveButton.getScene().getWindow()).close();
            }
        }
    }

    private int getSubjectIdByName(String name) {
        String sql = "SELECT id FROM subject WHERE subject = ? AND user_id = ?";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean addSchedule(int subjectId, String selectedDay, String startTime, String endTime) {
        String checkSql = "SELECT COUNT(*) FROM schedule WHERE user_id = ? AND day = ? AND start_time = ? AND end_time = ?";
        String insertSql = "INSERT INTO schedule (subject_id, user_id, day, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnectionManager.getConnection()) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, session.getUserId());
                checkStmt.setString(2, selectedDay);
                checkStmt.setString(3, startTime);
                checkStmt.setString(4, endTime);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, subjectId);
                pstmt.setInt(2, session.getUserId());
                pstmt.setString(3, selectedDay);
                pstmt.setString(4, startTime);
                pstmt.setString(5, endTime);

                return pstmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //

    @FXML
    private void cancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
