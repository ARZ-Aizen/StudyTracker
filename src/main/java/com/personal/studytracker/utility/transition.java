package com.personal.studytracker.utility;

import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Interpolator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class transition {

    public static void effects(Parent currentRoot, String fxmlPath, String title, boolean fullscreen) {
        try {
            Stage stage = (Stage) currentRoot.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(transition.class.getResource(fxmlPath));
            Parent nextRoot = loader.load();

            Scene scene = new Scene(nextRoot);
            stage.setScene(scene);
            stage.setTitle(title);

            if (fullscreen) {
                stage.setResizable(true);
            } else {
                stage.setResizable(false);
            }

            applyWindowPop(nextRoot);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyWindowPop(Parent root) {
        root.setOpacity(0.0);
        root.setScaleX(0.95);
        root.setScaleY(0.95);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), root);
        scale.setToX(1.0);
        scale.setToY(1.0);

        scale.setInterpolator(Interpolator.SPLINE(0.1, 1.0, 0.1, 1.0));

        FadeTransition fade = new FadeTransition(Duration.millis(20), root);
        fade.setToValue(1.0);

        ParallelTransition combined = new ParallelTransition(scale, fade);
        combined.play();
    }

    public static void popupEffects(Parent ownerRoot, String fxmlPath, String title) {
        try {
            Stage ownerStage = (Stage) ownerRoot.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(transition.class.getResource(fxmlPath));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(title);

            popupStage.initOwner(ownerStage);

            popupStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(popupRoot);
            popupStage.setScene(scene);
            popupStage.setResizable(false);

            applyWindowPop(popupRoot);

            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading popup FXML: " + fxmlPath);
        }
    }
}