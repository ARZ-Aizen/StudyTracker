package com.personal.studytracker.dashboard.ui;

import com.personal.studytracker.MainApplication;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML
    private AnchorPane homeView, courseView, taskView, scheduleView, historyView, settingsView, aboutView;

    @FXML
    private Button btnHome, btnCourses, btnTasks, btnSchedule, btnHistory, btnSettings, btnAbout;

    private List<AnchorPane> allViews;
    private List<Button> allButtons;

    @FXML
    public void initialize() {
        allViews = List.of(homeView, courseView, taskView, scheduleView, historyView, settingsView, aboutView);
        allButtons = List.of(btnHome, btnCourses, btnTasks, btnSchedule, btnHistory, btnSettings, btnAbout);

        homeView.setVisible(true);
        highlightButton(btnHome);
    }

    @FXML
    private void handleMenu(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String menuText = clickedButton.getText();

        allViews.forEach(view -> view.setVisible(false));
        allButtons.forEach(this::resetButton);

        highlightButton(clickedButton);

        switch (menuText) {
            case "Home" -> showView(homeView);
            case "Courses" -> showView(courseView);
            case "Tasks" ->  showView(taskView);
            case "Schedule" -> showView(scheduleView);
            case "History" ->  showView(historyView);
            case "Settings" -> showView(settingsView);
            case "About" -> showView(aboutView);
            case "Logout" -> handleLogout();
        }
    }

    private void showView (AnchorPane view) {
        view.setVisible(true);
        view.toFront();
    }

    private void highlightButton(Button btn) {
        btn.setStyle("-fx-background-color: #a16262; -fx-text-fill: white; -fx-background-radius: 10px; -fx-cursor: hand;");
    }

    private void resetButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-cursor: hand;");
    }

    @FXML private void handleLogout() {
        Parent root = btnHome.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login");
    }
    }

