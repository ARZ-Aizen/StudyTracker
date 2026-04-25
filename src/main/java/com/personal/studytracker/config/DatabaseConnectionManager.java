package com.personal.studytracker.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnectionManager {

    private static final String DATABASE_URL = "jdbc:sqlite:studytracker.db";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
        }

        public static void initializeDbFile() {
            try (Connection conn  = getConnection()) {
            } catch (SQLException e) {
                System.err.println("Could not create database file: " + e.getMessage());
            }
        }
    }
