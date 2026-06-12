/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author ntu-user
 */
public class FileUpdateController {
    @FXML private TextField fileNameInput;
    @FXML private TextArea fileContentInput;
    @FXML private Button saveFileButton;
    @FXML private Button deleteFileButton;
    @FXML private Label fileAccessLevelLabel;
    
    private String sessionToken;
    private FileOperation file;
    private DB db = new DB();
    private String accessPermissions;

    // ✅ Set session token and initialize the file editor based on permission
    public void setSessionTokenAndFile(String sessionToken, File file) {
        this.sessionToken = sessionToken;
        this.file = file;
        fileNameInput.setText(file.getFileName());
        fileContentInput.setText(extractAndReadFile(file.getDocContent()));
        
        checkUserPermission();
    }
    
    
    private String extractAndReadFile(byte[] zipBytes) {
        try {
            // Write ZIP bytes to a temporary file
            String tempZipPath = "temp.zip";
            Files.write(Paths.get(tempZipPath), zipBytes);

            // Extract the ZIP file
            ZipFile zipFile = new ZipFile(tempZipPath, ZIP_PASSWORD.toCharArray());
            String outputPath = "temp_extract/";
            zipFile.extractAll(outputPath);

            // Read the extracted text file
            java.io.File extractedFile = new java.io.File(outputPath + zipFile.getFileHeaders().get(0).getFileName());
            return new String(Files.readAllBytes(extractedFile.toPath()));

        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading file.";
        }
    }

    

    private void checkUserPermission() {
        String username = db.getAccountDetails(sessionToken).getUsername(); // Logged-in user
        String fileOwner = file.getUsername(); // Owner of the file

        // If the logged-in user is the file owner, they have Full Access
        if (username.equals(fileOwner)) {
            accessPermissions = "Full Access";
        } else {
            accessPermissions = db.fetchUserFilePermission(file.getDocId(), username); // Get stored permission
        }

        // Apply permission settings
        switch (accessPermissions.toLowerCase()) {
            case "full access":
                fileNameInput.setEditable(true);
                fileContentInput.setEditable(true);
                saveFileButton.setDisable(false);
                deleteFileButton.setDisable(false);
                fileAccessLevelLabel.setText("Full Access: You can edit and delete this file.");
                break;
            case "write":
                fileNameInput.setEditable(true);
                fileContentInput.setEditable(true);
                saveFileButton.setDisable(false);
                deleteFileButton.setDisable(true);
                fileAccessLevelLabel.setText("Write Access: You can edit but not delete.");
                break;
            default:
                fileNameInput.setEditable(false);
                fileContentInput.setEditable(false);
                saveFileButton.setDisable(true);
                deleteFileButton.setDisable(true);
                fileAccessLevelLabel.setText("Read-Only: You cannot edit or delete this file.");
                break;
        }
    }


    // ✅ Save file changes
    @FXML
    private void UpdateFileToDataBase() {
        String newFileName = fileNameInput.getText().trim();
        String modifiedFileName = fileContentInput.getText().trim();
        
        if (newFileName.isEmpty() || modifiedFileName.isEmpty()) {
            showAlert("Warning", "Filename and content cannot be empty.");
            return;
        }
        
        db.editFile(file.getFileId(), newFileName, modifiedFileName);
        showAlert("Success", "File updated successfully.");
    }

    // ✅ Delete file
    @FXML
    private void DeleteFileHandler() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm File Deletion");
        alert.setHeaderText("Are you sure you want to delete this file?");
        alert.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            db.removeFile(file.getFileId());
            showAlert("Success", "File deleted successfully.");
            closeWindow();
        }
    }
    
    // ✅ Close the editor window after deletion
    private void closeWindow() {
        Stage stage = (Stage) fileNameInput.getScene().getWindow();
        stage.close();
    }
    
    // ✅ Show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
}
