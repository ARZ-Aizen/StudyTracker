package com.personal.studytracker.dashboard.ui;

import com.personal.studytracker.config.databaseConnectionManager;
import com.personal.studytracker.model.Task;
import com.personal.studytracker.utility.session;
import com.personal.studytracker.utility.transition;
import com.personal.studytracker.window.scheduleCard;
import com.personal.studytracker.window.subjectCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
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
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colName, colSubject, colDeadline, colPriority, colStatus;

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

        setupEditableTable();

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

    public void setupEditableTable() {
        taskTable.setEditable(true);

        // 1. Task Name: Editable by keyboard
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setName(event.getNewValue());
            updateTaskField(task.getId(), "task_name", event.getNewValue());
        });

        colDeadline.setCellFactory(column -> new TableCell<Task, String>() {
            private final DatePicker datePicker = new DatePicker();

            {
                datePicker.setOnAction(e -> {
                    if (datePicker.getValue() != null) {
                        commitEdit(datePicker.getValue().toString());
                    }
                });
                datePicker.setStyle("-fx-font-family: 'Inter';");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        setGraphic(datePicker);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(item);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                setGraphic(datePicker);
                setText(null);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }
        });

        colDeadline.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setDeadline(event.getNewValue());
            updateTaskField(task.getId(), "deadline", event.getNewValue());
        });

        colPriority.setCellFactory(ComboBoxTableCell.forTableColumn("High", "Medium", "Low"));
        colPriority.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setPriority(event.getNewValue());
            updateTaskField(task.getId(), "priority", event.getNewValue());
        });

        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn("To do", "Completed", "Late"));
        colStatus.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            task.setStatus(event.getNewValue());
            updateTaskField(task.getId(), "status", event.getNewValue());
        });

        colSubject.setCellFactory(ComboBoxTableCell.forTableColumn(getSubjectNames()));
    }

    private ObservableList<String> getSubjectNames() {
        ObservableList<String> subjects = FXCollections.observableArrayList();
        String sql = "SELECT subject FROM subject WHERE user_id = ?";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, session.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) subjects.add(rs.getString("subject"));
        } catch (SQLException e) { e.printStackTrace(); }
        return subjects;
    }

    private int getSubjectIdByName(String name) {
        String sql = "SELECT id FROM subject WHERE subject = ? AND user_id = ?";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, session.getUserId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
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
                loadTasks();
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
    private void addTaskButton() {
        String sql = "INSERT INTO tasks (user_id, task_name, status) VALUES (?, 'New Task', 'To Do')";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getUserId());
            pstmt.executeUpdate();

            loadTasks();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTaskField(int taskId, String column, String newValue) {
        String sql = "UPDATE tasks SET " + column + " = ? WHERE task_id = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newValue);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
            System.out.println("Task updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "DELETE FROM tasks WHERE task_id = ?";
        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadTasks();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadTasks() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        String sql = "SELECT t.task_id, t.task_name, s.subject, t.deadline, t.priority, t.status " +
                "FROM tasks t " +
                "LEFT JOIN subject s ON t.subject_id = s.id " +
                "WHERE t.user_id = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                taskList.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("task_name"),
                        rs.getString("subject"),
                        rs.getString("deadline"),
                        rs.getString("priority"),
                        rs.getString("status")
                ));
            }

            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
            colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
            colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            taskTable.setItems(taskList);

        } catch (SQLException e) {
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
                    loadSchedules();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addScheduleToUI(int id, String name, String day, String start, String end) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personal/studytracker/window/schedule-card.fxml"));
            HBox card = loader.load();
            scheduleCard controller = loader.getController();
            controller.setData(id, name, day, start, end, this::loadSchedules);
            scheduleListContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSchedules() {
        scheduleListContainer.getChildren().clear();

        String sql = "SELECT s.schedule_id, s.day, s.start_time, s.end_time, sub.subject " +
                "FROM schedule s " +
                "JOIN subject sub ON s.subject_id = sub.id " +
                "WHERE s.user_id = ?";

        try (Connection conn = databaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, session.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                addScheduleToUI(
                        rs.getInt("schedule_id"),
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

