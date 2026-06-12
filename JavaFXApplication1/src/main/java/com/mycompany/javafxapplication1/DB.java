/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.lingala.zip4j.ZipFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.IOException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.UUID;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ntu-user
 */
public class DB {
    // MySQL connection 
    private static final String DATABASE_NAME = "Assignment";
    private static final String DATABASE_USER = "admin";
    private static final String DATABASE_PASSWORD = "nCMtV0HToXwr";
    private static final String DATABASE_URL = "jdbc:mysql://lamp-server:3306/" + DATABASE_NAME + "?useSSL=false";
    
    private static Connection establishMySQLConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }
    
    private String fileName = "jdbc:sqlite:./comp20081.db";
    private static Connection establishSQLiteConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:./comp20081.db");
    }

    private int timeout = 30;
    private String dataBaseName = "COMP20081";
    private String dataBaseTableName = "Users";
    Connection connection = null;
    private Random random = new SecureRandom();
    private String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private int iterations = 10000;
    private int keylength = 256;
    private String saltValue;
    private static final String ZIP_PASSWORD = "SuperUserZip";
    
    /**
     * @brief constructor - generates the salt if it doesn't exists or load it from the file .salt
     */
    DB() {
        try {
            File fp = new File(".salt");
            if (!fp.exists()) {
                saltValue = this.getSaltvalue(30);
                FileWriter myWriter = new FileWriter(fp);
                myWriter.write(saltValue);
                myWriter.close();
            } else {
                Scanner myReader = new Scanner(fp);
                while (myReader.hasNextLine()) {
                    saltValue = myReader.nextLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * @brief create a new table
     * @param tableName name of type String
     */
    public void createTable(String tableName) throws ClassNotFoundException {
        try {
            // create a database connection
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(fileName);
            String sql = "CREATE TABLE IF NOT EXISTS  " + this.dataBaseTableName + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "sessionToken TEXT)";
            
            // Create Document table
String fileTableSQL = "CREATE TABLE IF NOT EXISTS FileOperation ("
        + "doc_id VARCHAR(36) PRIMARY KEY, "
        + "doc_user VARCHAR(255) NOT NULL, "
        + "doc_name TEXT NOT NULL, "
        + "doc_path TEXT NULL, "
        + "doc_content BLOB, "
        + "doc_creation_date DATETIME DEFAULT CURRENT_TIMESTAMP, "
        + "doc_modification_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
        + "FOREIGN KEY (doc_user) REFERENCES Users(username) ON DELETE CASCADE"
        + ")";

// Create DocumentPermission table
String fileAuthorisationTableSQL = "CREATE TABLE IF NOT EXISTS FileAuthorisation ("
        + "permission_id VARCHAR(36) PRIMARY KEY, "
        + "document_id VARCHAR(36) NOT NULL, "
        + "username VARCHAR(255) NOT NULL, "
        + "document_owner VARCHAR(255) NOT NULL, "
        + "access_level VARCHAR(50) NOT NULL, "
        + "FOREIGN KEY (document_id) REFERENCES FileOperation(doc_id) ON DELETE CASCADE, "
        + "FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE, "
        + "FOREIGN KEY (document_owner) REFERENCES Users(username) ON DELETE CASCADE"
        + ")";
  

        try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(timeout);
                statement.executeUpdate(sql);
                statement.execute(fileTableSQL);      
                statement.execute(fileAuthorisationTableSQL);
            }

            System.out.println("Users table checked/created successfully.");

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * @brief delete table
     * @param tableName of type String
     */
    public void delTable(String tableName) throws ClassNotFoundException {
        try {
            // create a database connection
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(fileName);
            var statement = connection.createStatement();
            statement.setQueryTimeout(timeout);
            statement.executeUpdate("drop table if exists " + tableName);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * @brief add data to the database method
     * @param user name of type String
     * @param password of type String
     */
        public void addDataToDB(User user) throws InvalidKeySpecException, ClassNotFoundException {
            // firstly,inside your addDataToDB function delete the whole code inside it and paste this instead
String sql = "INSERT INTO Users (username, password,sessionToken) VALUES (?, ?, ?)";

        try (Connection mysqlConn = establishMySQLConnection(); Connection sqliteConn = establishSQLiteConnection()) {
            try (PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql); PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {
                pstmtMySQL.setString(1, user.getUsername());
                pstmtMySQL.setString(2, generateSecurePassword(user.getPassword()));
                pstmtMySQL.setString(3, user.getSessionToken());
                pstmtMySQL.executeUpdate();

               
                pstmtSQLite.setString(1, user.getUsername());
                pstmtSQLite.setString(2, generateSecurePassword(user.getPassword()));
                pstmtSQLite.setString(3, user.getSessionToken());
                pstmtSQLite.executeUpdate();
            }
            System.out.println("User added successfully in both databases: " + user.getUsername());
        } catch (SQLException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
    // function to create a session token
    private String createSessionToken(Connection conn, String username) throws SQLException {
        String sessionToken = UUID.randomUUID().toString();
        String updateSql = "UPDATE Users SET sessionToken = ? WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, sessionToken);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
        return sessionToken;
    }
    
    //function to generate a unique token for users session
    public String generateSessionToken() {
       return UUID.randomUUID().toString(); // Generates a unique session token
    }
    

    /**
     * @brief get data from the Database method
     * @retunr results as ResultSet
     */
    public ObservableList<User> getDataFromTable() throws ClassNotFoundException {
        ObservableList<User> result = FXCollections.observableArrayList();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(fileName);
            var statement = connection.createStatement();
            statement.setQueryTimeout(timeout);
            ResultSet rs = statement.executeQuery("select * from " + this.dataBaseTableName);
            while (rs.next()) {
                // read the result set
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return result;
    }

    /**
     * @brief decode password method
     * @param user name as type String
     * @param pass plain password of type String
     * @return true if the credentials are valid, otherwise false
     */
    public String validateUser(String username, String password) throws InvalidKeySpecException, ClassNotFoundException {
        String sessionToken = null;
        String sql = "SELECT password FROM Users WHERE username = ?";
        
        try (Connection sqliteConn = establishSQLiteConnection()) {
            try (PreparedStatement pstmt = sqliteConn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword.equals(generateSecurePassword(password))) {
                        return createSessionToken(sqliteConn, username);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try (Connection mysqlConn = establishMySQLConnection()) {
            try (PreparedStatement pstmt = mysqlConn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword.equals(generateSecurePassword(password))) {
                        return createSessionToken(mysqlConn, username);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSaltvalue(int length) {
        StringBuilder finalval = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            finalval.append(characters.charAt(random.nextInt(characters.length())));
        }

        return new String(finalval);
    }

    /* Method to generate the hash value */
    private byte[] hash(char[] password, byte[] salt) throws InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keylength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public String generateSecurePassword(String password) throws InvalidKeySpecException {
        String finalval = null;

        byte[] securePassword = hash(password.toCharArray(), saltValue.getBytes());

        finalval = Base64.getEncoder().encodeToString(securePassword);

        return finalval;
    }

    /**
     * @brief get table name
     * @return table name as String
     */
    public String getTableName() {
        return this.dataBaseTableName;
    }

    /**
     * @brief print a message on screen method
     * @param message of type String
     */
    public void log(String message) {
        System.out.println(message);

    }
    
    public void logOutUserFunctionality(String sessionToken) {
        String sql = "UPDATE Users SET sessionToken = NULL WHERE sessionToken = ?";

        try (Connection mysqlConn = establishMySQLConnection(); Connection sqliteConn = establishSQLiteConnection()) {
            try (PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
                 PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {

                pstmtMySQL.setString(1, sessionToken);
                pstmtMySQL.executeUpdate();

                pstmtSQLite.setString(1, sessionToken);
                pstmtSQLite.executeUpdate();
            }
            System.out.println("User logged out successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// function to delete users account
    public void deleteAccount(String sessionToken) {
        String sql = "DELETE FROM Users WHERE sessionToken = ?";

        try (Connection mysqlConn = establishMySQLConnection(); Connection sqliteConn = establishSQLiteConnection()) {
            try (PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
                 PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {

                pstmtMySQL.setString(1, sessionToken);
                pstmtMySQL.executeUpdate();

                pstmtSQLite.setString(1, sessionToken);
                pstmtSQLite.executeUpdate();
            }
            System.out.println("User account deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// function to get the users details
public User getAccountDetails(String sessionToken) {
    String sql = "SELECT username,password,sessionToken FROM Users WHERE sessionToken = ?";
    User user = null;

    try (Connection conn = establishMySQLConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, sessionToken);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password");
            String token = rs.getString("sessionToken");
            
            user = new User(username,password,token);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return user;
}




// function to search the database if the same username exists or not
public boolean doesUsernameExists(String newUsername, String sessionToken) {
    String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND sessionToken <> ?";

    try (Connection mysqlConn = establishMySQLConnection(); 
         Connection sqliteConn = establishSQLiteConnection()) {

        // Check in MySQL
        try (PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql)) {
            pstmtMySQL.setString(1, newUsername);
            pstmtMySQL.setString(2, sessionToken);
            ResultSet rsMySQL = pstmtMySQL.executeQuery();
            if (rsMySQL.next() && rsMySQL.getInt(1) > 0) {
                System.out.println("Username exists in MySQL.");
                return true;
            }
        }

        // Check in SQLite
        try (PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {
            pstmtSQLite.setString(1, newUsername);
            pstmtSQLite.setString(2, sessionToken);
            ResultSet rsSQLite = pstmtSQLite.executeQuery();
            if (rsSQLite.next() && rsSQLite.getInt(1) > 0) {
                System.out.println("Username exists in SQLite.");
                return true;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}





// function to update the acount
public void updateUserDetails(String sessionToken, String newUsername, String newPassword) throws InvalidKeySpecException {
    // Check if the username already exists in the database (excluding the current user)
    if (doesUsernameExists(newUsername, sessionToken)) {
        System.out.println("Username already exists. Please choose a different one.");
        return;
    }

    // Construct SQL query
    String sql = "UPDATE Users SET username = ?" +
                 (newPassword.isEmpty() ? "" : ", password = ?") + 
                 " WHERE sessionToken = ?";

    try (Connection mysqlConn = establishMySQLConnection(); Connection sqliteConn = establishSQLiteConnection()) {
        try (PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
             PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {

                pstmtMySQL.setString(1, newUsername);
            if (!newPassword.isEmpty()) {
                pstmtMySQL.setString(2, generateSecurePassword(newPassword));
                pstmtMySQL.setString(3, sessionToken);
            } else {
                pstmtMySQL.setString(2, sessionToken);
            }
            int mysqlRows = pstmtMySQL.executeUpdate();


            pstmtSQLite.setString(1, newUsername);
            if (!newPassword.isEmpty()) {
                pstmtSQLite.setString(2, generateSecurePassword(newPassword));
                pstmtSQLite.setString(3, sessionToken);
            } else {
                pstmtSQLite.setString(2, sessionToken);
            }
            int sqliteRows = pstmtSQLite.executeUpdate();
            // Confirm if the update was successful
            if (mysqlRows > 0 || sqliteRows > 0) {
                System.out.println("User account updated successfully.");
            } else {
                System.out.println("Update failed. No rows were affected.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void addDocumentDataToDB(FileOperation file) {
    // Updated SQL query to match the new column names and table name
    String sql = "INSERT INTO FileOperation (doc_id, doc_user, doc_name, doc_path ,doc_content, doc_creation_date, doc_modification_date) "
               + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection mysqlConn = establishMySQLConnection(); Connection sqliteConn = establishSQLiteConnection()) {
        try (PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql); PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {
            String docId = UUID.randomUUID().toString(); // Generate a unique document ID
            
            // converting to byte
            String convertFileContent = new String(file.getDocContent(), StandardCharsets.UTF_8);
            byte[] compressedContent = compressAndEncryptFile(convertFileContent, file.getDocName());

       
            // Set values for MySQL
            pstmtMySQL.setString(1, docId);
            pstmtMySQL.setString(2, file.getDocUser());
            pstmtMySQL.setString(3, file.getDocName());
            pstmtMySQL.setString(4, file.getDocPath());
            pstmtMySQL.setBytes(5, file.getDocContent());
            pstmtMySQL.setTimestamp(6, file.getDocCreationDate());
            pstmtMySQL.setTimestamp(7, file.getDocModificationDate());
            pstmtMySQL.executeUpdate();

            // Set values for SQLite
            pstmtSQLite.setString(1, docId);
            pstmtSQLite.setString(2, file.getDocUser());
            pstmtSQLite.setString(3, file.getDocName());
            pstmtMySQL.setString(4, file.getDocPath());
            pstmtSQLite.setBytes(5, file.getDocContent());
            pstmtSQLite.setTimestamp(6, file.getDocCreationDate());
            pstmtSQLite.setTimestamp(7, file.getDocModificationDate());
            pstmtSQLite.executeUpdate();
        }
        System.out.println("Document added successfully in both databases with encrypted content.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
// Function to update the file 
public void editFile(String fileId, String newFileName, String newFileContent) {
    java.io.File directory = new java.io.File("files/");
    if (!directory.exists()) {
        directory.mkdirs();
    }

    try {
        // Create a temporary file with new content
        java.io.File tempFile = new java.io.File("files/" + newFileName + ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(newFileContent);
        writer.close();

        // Define ZIP parameters
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);

        // Create new ZIP file with encryption
        String zipFilePath = "files/" + newFileName + ".zip";
        ZipFile zipFile = new ZipFile(zipFilePath, ZIP_PASSWORD.toCharArray());
        zipFile.addFile(tempFile, zipParameters);

        // Delete the temporary text file after zipping
        tempFile.delete();

        // Read the ZIP file content into a byte array
        byte[] zipContent = Files.readAllBytes(Paths.get(zipFilePath));

        // Update database with new file name and new encrypted ZIP content
        String sql = "UPDATE FileOperation SET doc_name = ?, doc_path = ?, doc_content = ?, doc_modification_date = CURRENT_TIMESTAMP WHERE doc_id = ?";

        // Compress and Encrypt the new content
        byte[] compressedContent = compressAndEncryptFile(newFileContent, newFileName);

        try (Connection mysqlConn = establishMySQLConnection();
             Connection sqliteConn = establishSQLiteConnection();
             PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
             PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {

            pstmtMySQL.setString(1, newFileName);
            pstmtMySQL.setBytes(2, zipContent);  // Use zipContent (byte array)
            pstmtMySQL.setString(3, fileId);
            pstmtMySQL.executeUpdate();

            pstmtSQLite.setString(1, newFileName);
            pstmtSQLite.setBytes(2, zipContent);  // Use zipContent (byte array)
            pstmtSQLite.setString(3, fileId);
            pstmtSQLite.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } catch (IOException e) {
        e.printStackTrace(); // Handle the IOException here
}
   }
 private byte[] compressAndEncryptFile(String content, String fileName) {
        try {
            // ✅ Ensure directory exists before writing the file
            java.io.File tempDir = new java.io.File("temp/");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
 
            // ✅ Save content to a temporary text file
            String tempFilePath = "temp/" + fileName + ".txt";
            java.io.File tempFile = new java.io.File(tempFilePath);
            FileWriter writer = new FileWriter(tempFile);
            writer.write(content);
            writer.close();
 
            // ✅ Ensure file exists before adding it to ZIP
            if (!tempFile.exists()) {
                throw new IOException("Temp file does not exist: " + tempFilePath);
            }
 
            // ✅ Create ZIP file
            String zipFilePath = "temp/" + fileName + ".zip";
            ZipFile zipFile = new ZipFile(zipFilePath, ZIP_PASSWORD.toCharArray());
 
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
 
            // ✅ Add file to ZIP
            zipFile.addFile(tempFile, zipParameters);
 
            // ✅ Convert ZIP to byte array
            return Files.readAllBytes(Paths.get(zipFilePath));
 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
     }
        public boolean grantFilePermission(String fileId, String username, String fileOwner, String permissionLevel) {
    // Check if the user is an Admin

    String sql = "INSERT INTO FileAuthorisation (permission_id, document_id, username, document_owner, access_level) VALUES (?, ?, ?, ?, ?)";
    boolean success = false;

    // Insert into MySQL
    try (Connection mysqlConn = establishMySQLConnection();
         PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql)) {
        pstmtMySQL.setString(1, UUID.randomUUID().toString());
        pstmtMySQL.setString(2, fileId);
        pstmtMySQL.setString(3, username);
        pstmtMySQL.setString(4, fileOwner);
        pstmtMySQL.setString(5, permissionLevel);
        pstmtMySQL.executeUpdate();
        success = true;
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Insert into SQLite
    try (Connection sqliteConn = establishSQLiteConnection();
         PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {
        pstmtSQLite.setString(1, UUID.randomUUID().toString());
        pstmtSQLite.setString(2, fileId);
        pstmtSQLite.setString(3, username);
        pstmtSQLite.setString(4, fileOwner);
        pstmtSQLite.setString(5, permissionLevel);
        pstmtSQLite.executeUpdate();
        success = true;
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return success;
 }
 
 
    // function to get all usernames except the current logged in user
    public ObservableList<String> retrieveAllUsernamesExceptCurrent(String sessionToken) {
    ObservableList<String> users = FXCollections.observableArrayList();
    Set<String> userSet = new HashSet<>(); // Prevent duplicates
    String sql = "SELECT username FROM Users WHERE username NOT IN (SELECT username FROM Users WHERE sessionToken = ?)";

    try (Connection mysqlConn = establishMySQLConnection();
         Connection sqliteConn = establishSQLiteConnection();
         PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
         PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {

        // Check MySQL for users
        pstmtMySQL.setString(1, sessionToken);
        ResultSet rsMySQL = pstmtMySQL.executeQuery();
        while (rsMySQL.next()) {
            String username = rsMySQL.getString("username");
            if (!userSet.contains(username)) {
                userSet.add(username);
                users.add(username);
            }
        }

        // Check SQLite for users
        pstmtSQLite.setString(1, sessionToken);
        ResultSet rsSQLite = pstmtSQLite.executeQuery();
        while (rsSQLite.next()) {
            String username = rsSQLite.getString("username");
            if (!userSet.contains(username)) {
                userSet.add(username);
                users.add(username);
            }
        }

        // Debugging: Check if SQLite actually has users
        try (Statement stmtSQLite = sqliteConn.createStatement();
             ResultSet rsCheckSQLite = stmtSQLite.executeQuery("SELECT COUNT(*) FROM Users")) {
            if (rsCheckSQLite.next()) {
                int userCount = rsCheckSQLite.getInt(1);
                System.out.println("SQLite User Count: " + userCount);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    System.out.println("Final list of users (excluding current session): " + users);
    return users;
}


    // function for getting users file from the database
    public ObservableList<FileOperation> getUserDocuments(String username) {
        ObservableList<FileOperation> files = FXCollections.observableArrayList();
        Map<String, FileOperation> fileMap = new HashMap<>(); // Prevent duplicates

        // Query to get owned files
        String fileQuery = "SELECT doc_id, doc_user, doc_name, doc_path, doc_content, doc_creation_date, doc_modification_date FROM FileOperation WHERE username = ?";

        // Query to get shared files
        String sharedFileQuery = "SELECT f.doc_id, f.doc_user AS document_owner, f.doc_name,f.doc_path, f.doc_content, f.doc_creation_date, f.doc_modification_date " +
                                 "FROM FileOperation f " +
                                 "INNER JOIN FileAuthorisation p ON f.doc_id = p.document_id " +
                                 "WHERE p.username = ?";

        // Get owned files from MySQL
        try (Connection mysqlConn = establishMySQLConnection();
             PreparedStatement pstmt = mysqlConn.prepareStatement(fileQuery)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fileId = rs.getString("doc_id");
                byte[] fileContent = rs.getBytes("doc_content"); // Read binary file content
                String extractedContent = extractAndReadFile(fileContent); // Extract content from ZIP

                FileOperation file = new FileOperation(fileId, rs.getString("username"), rs.getString("doc_name"), 
                                     rs.getString("doc_path"), fileContent, rs.getTimestamp("doc_creation_date"), 
                                     rs.getTimestamp("doc_modification_date"));
                fileMap.put(fileId, file);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get shared files from MySQL
        try (Connection mysqlConn = establishMySQLConnection();
             PreparedStatement pstmt = mysqlConn.prepareStatement(sharedFileQuery)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fileId = rs.getString("doc_id");
                if (!fileMap.containsKey(fileId)) {
                    byte[] fileContent = rs.getBytes("doc_content"); // Read binary file content
                    String extractedContent = extractAndReadFile(fileContent); // Extract content from ZIP

                    FileOperation file = new FileOperation(fileId, rs.getString("doc_user"), rs.getString("doc_name"), 
                                         rs.getString("doc_path"), fileContent, rs.getTimestamp("doc_creation_date"), 
                                         rs.getTimestamp("doc_modification_date"));
                    fileMap.put(fileId, file);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        files.addAll(fileMap.values()); // Convert map to list
        System.out.println("Fetched files for user: " + username + " -> " + files.size() + " files.");

        return files;
    }
    public void removeFile(String fileId) {
    String sql = "DELETE FROM FileOperation WHERE doc_id = ?";
   
    try (Connection mysqlConn = establishMySQLConnection();
         Connection sqliteConn = establishSQLiteConnection();
         PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
         PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {
       
        pstmtMySQL.setString(1, fileId);
        pstmtMySQL.executeUpdate();
       
        pstmtSQLite.setString(1, fileId);
        pstmtSQLite.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
    
    
    
    
    

    public String fetchUserFilePermission(String fileId, String username) {
    String sql = "SELECT access_level FROM FileAuthorisation WHERE document_id = ? AND username = ?";
   
    try (Connection mysqlConn = establishMySQLConnection();
         Connection sqliteConn = establishSQLiteConnection();
         PreparedStatement pstmtMySQL = mysqlConn.prepareStatement(sql);
         PreparedStatement pstmtSQLite = sqliteConn.prepareStatement(sql)) {
       
        pstmtMySQL.setString(1, fileId);
        pstmtMySQL.setString(2, username);
        ResultSet rsMySQL = pstmtMySQL.executeQuery();
        if (rsMySQL.next()) {
            return rsMySQL.getString("access_level");
        }
       
        pstmtSQLite.setString(1, fileId);
        pstmtSQLite.setString(2, username);
        ResultSet rsSQLite = pstmtSQLite.executeQuery();
        if (rsSQLite.next()) {
            return rsSQLite.getString("access_level");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "Read";
}

    // extract and read from file
    private String extractAndReadFile(byte[] zipBytes) {
      try {
          String tempZipPath = "temp.zip";
          Files.write(Paths.get(tempZipPath), zipBytes); // Write ZIP content to a temp file

          ZipFile zipFile = new ZipFile(tempZipPath, ZIP_PASSWORD.toCharArray());
          String outputPath = "temp_extract/";
          zipFile.extractAll(outputPath);

          java.io.File extractedFile = new java.io.File(outputPath + zipFile.getFileHeaders().get(0).getFileName());
          return new String(Files.readAllBytes(extractedFile.toPath()));
      } catch (Exception e) {
          e.printStackTrace();
          return "Error reading file.";
      }
    }
    private String decryptContent(String encryptedContent) {
    // Apply decryption logic here. For now, Base64 decoding.
    byte[] decodedBytes = Base64.getDecoder().decode(encryptedContent);
    return new String(decodedBytes, StandardCharsets.UTF_8);
}
public int getUserIdFromSessionToken(String sessionToken) {
    String sql = "SELECT id FROM Users WHERE sessionToken = ?";
    try (Connection conn = establishMySQLConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, sessionToken);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1; // Return -1 if no user is found
}

}










//    public static void main(String[] args) throws InvalidKeySpecException {
//        DB myObj = new DB();
//        myObj.log("-------- Simple Tutorial on how to make JDBC connection to SQLite DB ------------");
//        myObj.log("\n---------- Drop table ----------");
//        myObj.delTable(myObj.getTableName());
//        myObj.log("\n---------- Create table ----------");
//        myObj.createTable(myObj.getTableName());
//        myObj.log("\n---------- Adding Users ----------");
//        myObj.addDataToDB("ntu-user", "12z34");
//        myObj.addDataToDB("ntu-user2", "12yx4");
//        myObj.addDataToDB("ntu-user3", "a1234");
//        myObj.log("\n---------- get Data from the Table ----------");
//        myObj.getDataFromTable(myObj.getTableName());
//        myObj.log("\n---------- Validate users ----------");
//        String[] users = new String[]{"ntu-user", "ntu-user", "ntu-user1"};
//        String[] passwords = new String[]{"12z34", "1235", "1234"};
//        String[] messages = new String[]{"VALID user and password",
//            "VALID user and INVALID password", "INVALID user and VALID password"};
//
//        for (int i = 0; i < 3; i++) {
//            System.out.println("Testing " + messages[i]);
//            if (myObj.validateUser(users[i], passwords[i], myObj.getTableName())) {
//                myObj.log("++++++++++VALID credentials!++++++++++++");
//            } else {
//                myObj.log("----------INVALID credentials!----------");
//            }
//        }
//    }
