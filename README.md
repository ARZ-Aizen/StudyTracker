# StudyTracker

A comprehensive desktop application designed to help students manage their subjects, tasks, and weekly schedules. Built with JavaFX, this application features a sleek, modern user interface and relies on a local SQLite database for fast, offline data storage.

## 📦 Download & Release
Don't want to compile the code from source? You can download the latest ready-to-run version directly!

1. Navigate to the **[Releases](../../releases)** tab on the right side of this repository.
2. Download the latest `StudyTracker-x.x.jar` file from the Assets section.
3. Ensure you have **Java 21 or higher** installed on your system.

✨ Features
User Authentication: Secure sign-up and login system with password management.

Task Management: Add, track, and manage academic tasks with deadlines, priorities, and status tracking.

Subject Tracking: Organize courses by subject names and codes.

Schedule Builder: Create and view weekly class schedules with specific start and end times.

Offline First: All data is stored locally on the user's machine, requiring no internet connection.

🛠️ Technologies Used

JavaFX: Graphical User Interface (GUI) framework.

SQLite: Embedded relational database management system.

Maven: Dependency management and project building.

UI Libraries:

JFoenix - Material Design components.

ControlsFX - High-quality UI controls.

Ikonli (FontAwesome 5) - Icon packs for UI elements.

ValidatorFX - Form validation.

🗄️ Database Structure
Core Tables:

users: Stores user credentials and security questions.

subject: Stores course information (cascades on user deletion).

tasks: Tracks assignments and deadlines (linked to subjects and users).

schedule: Manage weekly class timings (linked to subjects and users).

🚀 Building from Source
Prerequisites
To build and modify this application from the source code, you will need:

Java Development Kit (JDK) 21 or higher.
