package com.personal.studytracker.window;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class scheduleManage {

    //

    @FXML private Button saveButton, cancelButton;
    @FXML private ComboBox<String> subject, day, startTime, endTime;

    //

    private int editingScheduleId = -1;

    //

    public void setFields(int id, String subName, String dayVal, String start, String end) {
        this.editingScheduleId = id;
        subject.setValue(subName);
        day.setValue(dayVal);
        startTime.setValue(start);
        endTime.setValue(end);

        if (saveButton != null) {
            saveButton.setOnAction(e -> editProcess());
        }
    }

    @FXML public void initialize() {
        subject.getItems().addAll(getSubjectList(session.getUserId()));
        day.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        List<String> intervals = generateTimeIntervals(5, 21, 5);
        startTime.getItems().addAll(intervals);
        endTime.getItems().addAll(intervals);
    }

    //

    @FXML protected void addProcess() {
        if (isInputInvalid()) return;

        int subjectId = getSubjectIdByName(subject.getValue());
        if (addSchedule(subjectId, day.getValue(), startTime.getValue(), endTime.getValue())) {
            alerts.show(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(), "Success", "Added successfully!");
            ((Stage) saveButton.getScene().getWindow()).close();
        }
    }

    @FXML protected void editProcess() {
        if (isInputInvalid()) return;

        int subjectId = getSubjectIdByName(subject.getValue());
        if (updateSchedule(subjectId, day.getValue(), startTime.getValue(), endTime.getValue())) {
            alerts.show(Alert.AlertType.INFORMATION, saveButton.getScene().getWindow(), "Success", "Updated successfully!");
            ((Stage) saveButton.getScene().getWindow()).close();

        } else {
            alerts.show(Alert.AlertType.ERROR, saveButton.getScene().getWindow(), "Error", "Failed to update database.");
        }
    }

    //

    private boolean addSchedule(int subId, String d, String s, String e) {
        String sql = "INSERT INTO schedule (subject_id, user_id, day, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subId);
            pstmt.setInt(2, session.getUserId());
            pstmt.setString(3, d);
            pstmt.setString(4, s);
            pstmt.setString(5, e);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) { return false; }
    }

    private boolean updateSchedule(int subId, String d, String s, String e) {
        String sql = "UPDATE schedule SET subject_id = ?, day = ?, start_time = ?, end_time = ? WHERE schedule_id = ? AND user_id = ?";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subId);
            pstmt.setString(2, d);
            pstmt.setString(3, s);
            pstmt.setString(4, e);
            pstmt.setInt(5, editingScheduleId);
            pstmt.setInt(6, session.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) { return false; }
    }

    //

    @FXML private void cancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean isInputInvalid() {
        if (subject.getValue() == null || day.getValue() == null || startTime.getValue() == null || endTime.getValue() == null) {
            alerts.show(Alert.AlertType.ERROR, subject.getScene().getWindow(), "Error", "Fill all fields.");
            return true;
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("h:mm a");
        if (!LocalTime.parse(startTime.getValue(), f).isBefore(LocalTime.parse(endTime.getValue(), f))) {
            alerts.show(Alert.AlertType.ERROR, startTime.getScene().getWindow(), "Error", "End time must be after start time.");
            return true;
        }
        return false;
    }

    private int getSubjectIdByName(String name) {
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM subject WHERE subject = ? AND user_id = ?")) {
            ps.setString(1, name);
            ps.setInt(2, session.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException ex) { ex.printStackTrace(); }
        return -1;
    }

    private List<String> getSubjectList(int uId) {
        List<String> list = new ArrayList<>();
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT subject FROM subject WHERE user_id = ?")) {
            ps.setInt(1, uId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("subject"));
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    private List<String> generateTimeIntervals(int startH, int endH, int interval) {
        List<String> times = new ArrayList<>();
        for (int h = startH; h <= endH; h++) {
            for (int m = 0; m < 60; m += interval) {
                if (h == endH && m > 0) break;
                String p = (h < 12) ? "AM" : "PM";
                int dh = (h > 12) ? h - 12 : (h == 0 ? 12 : h);
                times.add(String.format("%d:%02d %s", dh, m, p));
            }
        }
        return times;
    }
}