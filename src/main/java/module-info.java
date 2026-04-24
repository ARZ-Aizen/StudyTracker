module com.personal.studytracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.personal.studytracker to javafx.fxml;

    opens com.personal.studytracker.dashboard.ui to javafx.fxml;
    opens com.personal.studytracker.identity.ui to javafx.fxml;
    opens com.personal.studytracker.study.ui to javafx.fxml;

    exports com.personal.studytracker;
}