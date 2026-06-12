/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import com.mycompany.javafxapplication1.User;

/**
 *
 * @author ntu-user
 */
public class FileOperationController {
    private String userSessionToken;
   
    @FXML
    private TextField documentNameInput;
   
    @FXML
    private TextArea documentContentInput;
   
    // Method to set session token when switching controllers
    public void setUserSessionToken(String userSessionToken) {
        this.userSessionToken = userSessionToken;
    }

    @FXML
    private void createDocument() {
        String documentName = documentNameInput.getText().trim();
        String documentContent = documentContentInput.getText().trim();
        String username = fetchCurrentUsername(); // Fetch the current user's username

        if (documentName.isEmpty() || documentContent.isEmpty()) {
            displayAlert("Error", "Please enter both a document name and content.");
            return;
        }

        try {
            // Save to a local file
            java.io.File file = new java.io.File("documents/" + documentName);
            file.getParentFile().mkdirs(); // Ensure directory exists
            FileWriter writer = new FileWriter(file);
            writer.write(documentContent);
            writer.close();

            // Save to database
            DB database = new DB();
            String documentId = UUID.randomUUID().toString();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            FileOperation newDocument = new FileOperation(documentId, username, documentName, documentContent, timestamp, timestamp);
            database.addDocumentDataToDB(newDocument);

            displayAlert("Success", "Document saved successfully: " + documentName);
        } catch (IOException e) {
            displayAlert("Error", "Error creating document.");
            e.printStackTrace();
        }
    }

    private String fetchCurrentUsername() {
        DB database = new DB();
        User user = database.getAccountDetails(userSessionToken);
        if (user != null) {
            return user.getUsername();
        }
        return "unknown_user"; // Fallback if the user isn't found
    }

    private void displayAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearInputFields() {
        documentNameInput.clear();
        documentContentInput.clear();
    }
}