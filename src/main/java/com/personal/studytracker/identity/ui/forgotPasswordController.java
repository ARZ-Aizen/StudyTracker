package com.personal.studytracker.identity.ui;

import com.personal.studytracker.utility.transition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

public class forgotPasswordController {

    //

    @FXML private TextField usernameField;
    @FXML private TextField answerField;

    //

    @FXML private void handleLogin() {
        Parent root = usernameField.getScene().getRoot();
        transition.effects(root, "/com/personal/studytracker/identity/ui/login-view.fxml", "Study Tracker - Login", false);
    }
}
