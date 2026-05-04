package com.personal.studytracker.model;

import javafx.beans.property.*;

public class Task {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty subject;
    private final StringProperty deadline;
    private final StringProperty priority;
    private final StringProperty status;

    public Task(int id, String name, String subject, String deadline, String priority, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.subject = new SimpleStringProperty(subject);
        this.deadline = new SimpleStringProperty(deadline);
        this.priority = new SimpleStringProperty(priority);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getSubject() { return subject.get(); }
    public String getDeadline() { return deadline.get(); }
    public String getPriority() { return priority.get(); }
    public String getStatus() { return status.get(); }

    public void setName(String value) { this.name.set(value); }
    public void setSubject(String value) { this.subject.set(value); }
    public void setDeadline(String value) { this.deadline.set(value); }
    public void setPriority(String value) { this.priority.set(value); }
    public void setStatus(String value) { this.status.set(value); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty subjectProperty() { return subject; }
    public StringProperty deadlineProperty() { return deadline; }
    public StringProperty priorityProperty() { return priority; }
    public StringProperty statusProperty() { return status; }
}