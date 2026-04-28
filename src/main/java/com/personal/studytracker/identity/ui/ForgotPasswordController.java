package com.personal.studytracker.identity.ui;

import com.personal.studytracker.config.DatabaseConnectionManager;
import com.personal.studytracker.utility.alerts;
import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class ForgotPasswordController {

    @FXML private TextField usernameField;
    @FXML private TextField answerField;


    @FXML
    private void handleLogin() {
        Parent root = usernameField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login");
    }
}
