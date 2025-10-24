package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProfileScreen {

    private final Main mainApp;
    private final int totalQuestions;
    private final Stage primaryStage;
    private ImageView profileImageView; // To display the profile image

    // Constructor is updated to accept the Stage
    public ProfileScreen(Main mainApp, Stage primaryStage, int totalQuestions) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
        this.totalQuestions = totalQuestions;
    }

    public Parent getView() {
        // Main layout
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #F8F8F8;");

        Label title = new Label("User Profile");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill: #333333;");

        // --- Profile Picture Section ---
        
        // Use a 150x150 placeholder
        profileImageView = new ImageView();
        profileImageView.setFitWidth(150);
        profileImageView.setFitHeight(150);
        profileImageView.setPreserveRatio(true);

        // Create a gray placeholder rectangle
        Rectangle placeholder = new Rectangle(150, 150);
        placeholder.setFill(Color.web("#CCCCCC"));
        // Clip the image view to be a circle
        Rectangle clip = new Rectangle(150, 150);
        clip.setArcWidth(150); // Make it circular
        clip.setArcHeight(150);
        profileImageView.setClip(clip);
        
        // Use a StackPane to show placeholder if no image is set
        StackPane imageContainer = new StackPane(placeholder, profileImageView);
        imageContainer.setPrefSize(150, 150);
        imageContainer.setStyle("-fx-background-radius: 75;"); // Circular background

        Button uploadButton = new Button("Upload Image");
        uploadButton.setStyle("-fx-background-color: #64B5F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        uploadButton.setOnAction(e -> handleImageUpload());
        
        VBox profilePicLayout = new VBox(15, imageContainer, uploadButton);
        profilePicLayout.setAlignment(Pos.CENTER);


        // --- Profile Details Section ---
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(15);
        detailsGrid.setAlignment(Pos.CENTER);
        detailsGrid.setMaxWidth(400);

        // Name
        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label nameValue = new Label("Karan Keche"); // <-- Your Name
        nameValue.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        detailsGrid.add(nameLabel, 0, 0);
        detailsGrid.add(nameValue, 1, 0);

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label emailValue = new Label("kechekaran@gmail.com"); // <-- Your Email
        emailValue.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        detailsGrid.add(emailLabel, 0, 1);
        detailsGrid.add(emailValue, 1, 1);
        
        // --- Stats Section ---
        Label statsLabel = new Label("Total Questions Saved:");
        statsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label statsValue = new Label(String.valueOf(totalQuestions));
        statsValue.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        detailsGrid.add(statsLabel, 0, 2);
        detailsGrid.add(statsValue, 1, 2);
        

        // Back Button
        Button backButton = new Button("Back to Companies");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 8;");
        backButton.setOnAction(e -> mainApp.showCompanyListScreen());

        layout.getChildren().addAll(title, profilePicLayout, detailsGrid, backButton);
        VBox.setVgrow(detailsGrid, Priority.ALWAYS); // Push back button to bottom

        return layout;
    }

    /**
     * Handles opening a FileChooser to select an image.
     */
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        
        // Set extension filters
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        
        if (selectedFile != null) {
            try {
                // Load the selected image into the ImageView
                Image image = new Image(new FileInputStream(selectedFile));
                profileImageView.setImage(image);
            } catch (FileNotFoundException e) {
                System.err.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

