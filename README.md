# Distributed File System (DFS)

A secure, desktop-based Distributed File System application built with JavaFX and Maven. This system demonstrates secure file storage, user authentication, and access control by syncing metadata between SQLite (local) and MySQL (remote) databases, and encrypting stored files.

---

## Key Features

* **Dual Database Synchronization**: Automatically syncs user credentials and file metadata between a local SQLite database (`comp20081.db`) and a remote MySQL server to maintain consistency across local and distributed environments.
* **Secure User Authentication**: Uses PBKDF2 hashing (`PBKDF2WithHmacSHA1`) combined with a unique salt value stored locally in a `.salt` file to protect user passwords. Supports user registration, profile updates, and account deletion.
* **Encrypted File Storage**: Compresses and encrypts files into AES-256 ZIP files using `Zip4j` before saving them as binary large objects (`BLOB`s) in the database.
* **Access Control & Sharing**: A robust sharing mechanism allows users to grant specific access levels (permissions) on their files to other users in the system. Users can view both their owned documents and documents shared with them.

---

## Technology Stack

* **Front-end**: JavaFX with FXML (GUI layout and controllers)
* **Build System**: Apache Maven
* **Databases**: SQLite (Local) & MySQL (Remote)
* **Security & Cryptography**: AES-256 Encryption (via Zip4j), PBKDF2 Hashing (for passwords)

---

## Project Structure

```
.
├── JavaFXApplication1/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/mycompany/javafxapplication1/
│   │       │       ├── App.java                      # Main application entry point
│   │       │       ├── DB.java                       # Database management, security, and operations
│   │       │       ├── FileOperationController.java  # Controller for file uploads/downloads
│   │       │       ├── FileUpdateController.java     # Controller for modifying existing files
│   │       │       ├── PrimaryController.java        # Main dashboard / login controller
│   │       │       ├── RegisterController.java       # User registration controller
│   │       │       ├── SecondaryController.java      # Dashboard and file viewer controller
│   │       │       └── User.java                     # User model
│   │       └── resources/
│   │           └── com/mycompany/javafxapplication1/
│   │               ├── primary.fxml                  # UI layout for primary/login view
│   │               ├── register.fxml                 # UI layout for registration view
│   │               └── secondary.fxml                # UI layout for dashboard/file view
│   ├── nbactions.xml                                 # NetBeans actions configuration
│   └── pom.xml                                       # Maven dependency descriptors
└── README.md
```

---

## Configuration & Setup

### Database Tables Schema
The application automatically checks and constructs the following SQLite / MySQL tables at startup:
1. **`Users`**: Holds username, hashed password, and session token.
2. **`FileOperation`**: Stores file ID, owner username, file name, path, and the AES-encrypted ZIP file contents as a `BLOB`.
3. **`FileAuthorisation`**: Tracks file sharing levels and usernames granted access.

### Prerequisites
* Java 17 or higher
* Maven 3.6+
* MySQL Server (optional, fallback to local SQLite is supported)

### Running the App
Navigate to the project directory and run with Maven:
```bash
cd JavaFXApplication1
mvn clean javafx:run
```
