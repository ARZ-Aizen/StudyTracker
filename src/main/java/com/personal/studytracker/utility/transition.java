package com.personal.studytracker.utility;

import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Interpolator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class transition {

    public static void effects(Parent currentRoot, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) currentRoot.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(transition.class.getResource(fxmlPath));
            Parent nextRoot = loader.load();

            Scene scene = new Scene(nextRoot);
            stage.setScene(scene);
            stage.setTitle(title);

            applyWindowPop(nextRoot);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyWindowPop(Parent root) {
        // Initial state: slightly smaller and transparent
        root.setOpacity(0.0);
        root.setScaleX(0.9);
        root.setScaleY(0.9);

        // Scale Up Animation
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), root);
        scale.setToX(1.0);
        scale.setToY(1.0);

        // Fix: Use Interpolator.SPLINE or a specific curve for the "pop"
        // 0.1, 1, 0.1, 1 creates a snappy pop effect
        scale.setInterpolator(Interpolator.SPLINE(0.1, 1.0, 0.1, 1.0));

        // Fade In Animation
        FadeTransition fade = new FadeTransition(Duration.millis(20), root);
        fade.setToValue(1.0);

        ParallelTransition combined = new ParallelTransition(scale, fade);
        combined.play();
    }
}