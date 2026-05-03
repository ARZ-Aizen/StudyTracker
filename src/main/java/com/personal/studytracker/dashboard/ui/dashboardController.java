package com.personal.studytracker.dashboard.ui;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.utility.session;
import com.personal.studytracker.utility.transition;
import com.personal.studytracker.window.scheduleCard;
import com.personal.studytracker.window.subjectCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class dashboardController {

    //

    @FXML private AnchorPane homeView, courseView, taskView, scheduleView, historyView, settingsView, aboutView;
    @FXML private StackPane mainStackPane;
    @FXML private Button btnHome, btnCourses, btnTasks, btnSchedule, btnHistory, btnSettings, btnAbout;

    //

    @FXML private Label helloUserHeader, completionRateLabel, dueTasksLabel, totalSubjectsLabel, todayDateLabel, valCompletion, valDue, valTotal, valDate;

    //

    @FXML private Label courseTitleLabel, courseSubLabel;
    @FXML private Button courseAddButton;
    @FXML private VBox subjectListContainer;

    //

    @FXML private Label tasksTitleLabel, tasksSubLabel;
    @FXML private Button addTaskButton;

    //

    @FXML private Label scheduleTitleLabel, scheduleSubLabel;
    @FXML private Button addSchduleButton;
    @FXML private VBox scheduleListContainer;

    //

    private List<AnchorPane> allViews;
    private List<Button> allButtons;

    //

    @FXML
    public void initialize() {
        //
        allViews = List.of(homeView, courseView, taskView, scheduleView, historyView, settingsView, aboutView);
        allButtons = List.of(btnHome, btnCourses, btnTasks, btnSchedule, btnHistory, btnSettings, btnAbout);

        //
        helloUserHeader.setText("Hello, " + session.getUsername() + "!");

        //
        allViews.forEach(view -> view.setVisible(false));
        homeView.setVisible(true);
        highlightButton(btnHome);

        //
        mainStackPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();

            //
            responsive(w, helloUserHeader,
                    List.of(completionRateLabel, dueTasksLabel, totalSubjectsLabel, todayDateLabel),
                    List.of(valCompletion, valDue, valTotal, valDate));

            //
            responsive(w, courseTitleLabel, courseSubLabel, courseAddButton);

            //
            responsive(w, tasksTitleLabel, tasksSubLabel, addTaskButton);

            //
            responsive(w, scheduleTitleLabel, scheduleSubLabel, addSchduleButton);
        });
    }

    //

    @FXML
    private void handleMenu(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String menuText = clickedButton.getText();

        allViews.forEach(view -> view.setVisible(false));
        allButtons.forEach(this::resetButton);

        highlightButton(clickedButton);

        switch (menuText) {
            case "Home" -> showView(homeView);
            case "Courses" -> {
                showView(courseView);
                loadCourse();
            }
            case "Tasks" -> {
                showView(taskView);
            }
            case "Schedule" -> {
                showView(scheduleView);
                loadSchedules();
            }
            case "History" -> showView(historyView);
            case "Settings" -> showView(settingsView);
            case "About" -> showView(aboutView);
            case "Logout" -> handleLogout();
        }
    }

    public void responsive(double width, Label header, List<Label> titles, List<Label> values) {
        double hSize = Math.clamp(width / 40, 24, 48);
        double tSize = Math.clamp(width / 80, 12, 18);
        double vSize = Math.clamp(width / 45, 18, 36);

        header.setStyle("-fx-font-size: " + hSize + "px; -fx-font-weight: 800;");

        String tStyle = "-fx-font-size: " + tSize + "px; -fx-font-weight: 700; -fx-text-fill: #a16262;";
        titles.forEach(t -> t.setStyle(tStyle));

        String vStyle = "-fx-font-size: " + vSize + "px; -fx-font-weight: 800;";
        values.forEach(v -> v.setStyle(vStyle));
    }

    public void responsive(double width, Label title, Label subtitle, Button actionBtn) {
        double tSize = Math.clamp(width / 40, 24, 48);
        double sSize = Math.clamp(width / 85, 12, 16);
        double bSize = Math.clamp(width / 90, 12, 16);

        title.setStyle("-fx-font-size: " + tSize + "px; -fx-font-weight: 800;");
        subtitle.setStyle("-fx-font-size: " + sSize + "px; -fx-text-fill: #666666;");

        actionBtn.setStyle("-fx-font-size: " + bSize + "px; " +
                "-fx-background-color: #a16262; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5px; " +
                "-fx-cursor: hand;");
    }

    private void showView(AnchorPane view) {
        view.setVisible(true);
        view.toFront();
    }

    private void highlightButton(Button btn) {
        btn.setStyle("-fx-background-color: #a16262; -fx-text-fill: white; -fx-background-radius: 10px; -fx-cursor: hand;");
    }

    private void resetButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-cursor: hand;");
    }

    @FXML
    private void handleLogout() {
        session.clear();
        Parent root = btnHome.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login", false);
    }

    //

    @FXML
    private void courseAddButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/course-add-view.fxml"));
            Parent root = loader.load();
            Parent ownerRoot = btnHome.getScene().getRoot();
            Stage popup = transition.popupWithRoot(ownerRoot, root, "Add Course");

            if (popup != null) {
                popup.setOnHidden(e -> {
                    ownerRoot.setEffect(null);
                    loadCourse();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCourse() {
        subjectListContainer.getChildren().clear();

        String query = "SELECT subject, code FROM subject WHERE user_id = ?";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("subject");
                String code = rs.getString("code");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/subject-card.fxml"));
                HBox card = loader.load();

                subjectCard cardController = loader.getController();
                cardController.setData(name, code);

                cardController.setRefreshCallback(this::loadCourse);

                card.setMaxWidth(Double.MAX_VALUE);
                subjectListContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //

    @FXML
    private void scheduleAddButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/schedule-add-view.fxml"));
            Parent root = loader.load();
            Parent ownerRoot = btnHome.getScene().getRoot();
            Stage popup = transition.popupWithRoot(ownerRoot, root, "Add Schedule");

            if (popup != null) {
                popup.setOnHidden(e -> {
                    ownerRoot.setEffect(null);
                    loadCourse();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addScheduleToUI(String name, String day, String start, String end) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/schedule-card.fxml"));
            HBox card = loader.load();

            scheduleCard controller = loader.getController();
            controller.setData(name, day, start, end);

            scheduleListContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSchedules() {
        scheduleListContainer.getChildren().clear();

        String sql = "SELECT s.day, s.start_time, s.end_time, sub.subject " +
                "FROM schedule s " +
                "JOIN subject sub ON s.subject_id = sub.id " +
                "WHERE s.user_id = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                addScheduleToUI(
                        rs.getString("subject"),
                        rs.getString("day"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

