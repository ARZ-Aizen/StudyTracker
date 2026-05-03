package com.personal.studytracker.window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class scheduleCard {
    @FXML private Label cardSubjectName;
    @FXML private Label cardSubjectCode;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    public void setData(String name, String day, String start, String end) {
        cardSubjectName.setText(name);
        cardSubjectCode.setText(String.format("%s (%s - %s)", day, start, end));
        cardSubjectName.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        cardSubjectCode.setStyle("-fx-text-fill: #666666;");
    }
}