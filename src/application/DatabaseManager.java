package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // --- Database Configuration ---
    // !! IMPORTANT !!
    // !! Update this password to your MySQL root password !!
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Karan@s2004"; // <-- CHANGE THIS
    
    // 1. Database URL (points to the specific database)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/interview_tracker";
    // 2. Connection URL (points to the MySQL server, used for creating the DB)
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";

    // --- Constructor ---
    public DatabaseManager() {
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            createDatabase(); // Ensure database exists
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    /**
     * Creates the 'interview_tracker' database if it doesn't already exist.
     */
    private void createDatabase() {
        // Use SERVER_URL to connect to the server itself
        try (Connection conn = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            String sql = "CREATE DATABASE IF NOT EXISTS interview_tracker";
            stmt.executeUpdate(sql);
            // System.out.println("Database 'interview_tracker' checked/created successfully.");

        } catch (SQLException e) {
            System.err.println("Error creating/checking database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the 'interview_tracker' database.
     * @return A Connection object or null if connection fails.
     */
    private Connection getConnection() throws SQLException {
        // Use DB_URL to connect to the specific database
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Sanitizes a company name to be used as a valid SQL table name.
     * Replaces spaces, ampersands, and other symbols with underscores.
     * @param companyName The raw company name.
     * @return A safe table name.
     */
    private String sanitizeTableName(String companyName) {
        // Replaces any character that is NOT a letter, number, or underscore with an underscore
        return companyName.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * Iterates through the list of companies and creates a table for each
     * if it doesn't already exist.
     * @param companies List of company names.
     */
    public void createAllCompanyTables(List<String> companies) {
        String sqlTemplate = "CREATE TABLE IF NOT EXISTS %s ("
                           + "id INT AUTO_INCREMENT PRIMARY KEY,"
                           + "question_text TEXT NOT NULL,"
                           + "difficulty VARCHAR(50),"
                           + "type VARCHAR(50)"
                           + ")";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String company : companies) {
                String tableName = sanitizeTableName(company);
                String sql = String.format(sqlTemplate, tableName);
                stmt.execute(sql);
            }
            // System.out.println("All company tables checked/created successfully.");

        } catch (SQLException e) {
            System.err.println("Error creating company tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a new interview question to a specific company's table.
     * @param companyName The company to add the question to.
     * @param question The Question object to add.
     * @return true if successful, false otherwise.
     */
    public boolean addQuestion(String companyName, Question question) {
        String tableName = sanitizeTableName(companyName);
        String sql = "INSERT INTO " + tableName + " (question_text, difficulty, type) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getDifficulty());
            pstmt.setString(3, question.getType());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all questions for a specific company.
     * @param companyName The company to retrieve questions for.
     * @return A List of Question objects.
     */
    public List<Question> getQuestions(String companyName) {
        List<Question> questions = new ArrayList<>();
        String tableName = sanitizeTableName(companyName);
        // [UPDATED] Select the 'id' column as well
        String sql = "SELECT id, question_text, difficulty, type FROM " + tableName;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("question_text");
                String difficulty = rs.getString("difficulty");
                String type = rs.getString("type");
                
                // [UPDATED] Use the new constructor with the ID
                questions.add(new Question(id, text, difficulty, type));
            }

        } catch (SQLException e) {
            System.err.println("Error getting questions: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }
    
    /**
     * [NEW] Updates an existing question in the database.
     * @param companyName The company table where the question resides.
     * @param question The Question object containing the updated data (must have a valid ID).
     * @return true if successful, false otherwise.
     */
    public boolean updateQuestion(String companyName, Question question) {
        String tableName = sanitizeTableName(companyName);
        String sql = "UPDATE " + tableName + " SET question_text = ?, difficulty = ?, type = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getDifficulty());
            pstmt.setString(3, question.getType());
            pstmt.setInt(4, question.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * [NEW] Deletes a question from the database by its ID.
     * @param companyName The company table where the question resides.
     * @param questionId The ID of the question to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteQuestion(String companyName, int questionId) {
        String tableName = sanitizeTableName(companyName);
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [NEW] Gets a total count of all questions saved across all company tables.
     * @param companyList The list of all companies to check.
     * @return The total number of questions as an integer.
     */
    public int getTotalQuestionCount(List<String> companyList) {
        int totalCount = 0;
        String sqlTemplate = "SELECT COUNT(*) FROM %s";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String company : companyList) {
                String tableName = sanitizeTableName(company);
                String sql = String.format(sqlTemplate, tableName);
                
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        totalCount += rs.getInt(1); // Add the count from this table
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total question count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalCount;
    }
}

