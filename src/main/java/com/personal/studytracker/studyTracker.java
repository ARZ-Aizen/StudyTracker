    package com.personal.studytracker;

    import com.personal.studytracker.config.databaseConnectionManager;
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.stage.Stage;
    import java.io.IOException;



    public class studyTracker extends Application {

        @Override
        public void init() throws Exception {
            databaseConnectionManager.initializeDbFile();
        }

        @Override
        public void start(Stage stage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(studyTracker.class.getResource("identity/ui/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Study Tracker - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }

        public static void main(String[] args) {
            launch();
        }
    }