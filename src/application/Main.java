package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout; // <-- Main layout is now a BorderPane
    private DatabaseManager dbManager;
    
    // Master list of companies
    private final List<String> companyList = new ArrayList<>(Arrays.asList(
            "Accenture", "Adobe", "Amazon", "AMD", "Apple", "Applied Materials",
            "Bosch", "Capgemini", "CGI", "Cisco", "Cognizant", "Dell", "Deloitte",
            "DXC Technology", "Ericsson", "EY", "Genpact", "Google", "HCLTech",
            "HP", "HTC", "Huawei", "IBM", "Infosys", "Intel", "Jio",
            "JPMorgan Chase", "KPMG", "Larsen & Toubro", "Lenovo", "LTIMindtree",
            "MediaTek", "Meta", "Microsoft", "Netflix", "Nokia", "Nvidia",
            "Oracle", "Persistent Systems", "PwC", "Qualcomm", "Reliance",
            "Samsung", "SAP", "Sony", "TCS", "Tech Mahindra", "Tesla",
            "Tiger Analytics", "Wipro", "Xiaomi", "Zoho", "Zomato", "ZS Associates"
    ));

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Interview Question Tracker");
        this.primaryStage.setMinWidth(1000); // Increased width
        this.primaryStage.setMinHeight(700); // Increased height
        
        // Sort the company list
        Collections.sort(companyList);

        // --- Initialize Database ---
        this.dbManager = new DatabaseManager();
        dbManager.createAllCompanyTables(companyList); // Pass the list

        // --- Initialize Main Layout ---
        rootLayout = new BorderPane();

        // --- Create Professional Header ---
        HBox header = createHeader();
        rootLayout.setTop(header);

        // --- Set Initial Screen (Company List) ---
        showCompanyListScreen();

        // --- Setup Scene ---
        Scene scene = new Scene(rootLayout, 1100, 750); // Larger default size
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a professional header for the application.
     */
    private HBox createHeader() {
        Label title = new Label("IQT - Interview Question Tracker");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        // Spacer to push profile button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileButton = new Button("Profile");
        profileButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        profileButton.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #333333; -fx-background-radius: 8;");
        profileButton.setOnAction(e -> showProfileScreen());

        HBox header = new HBox(20, title, spacer, profileButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #333A45;"); // Dark professional header
        
        return header;
    }

    /**
     * Shows the Company List screen in the center of the root layout.
     */
    public void showCompanyListScreen() {
        CompanyListScreen companyScreen = new CompanyListScreen(this, companyList);
        rootLayout.setCenter(companyScreen.getView());
    }

    /**
     * Shows the Add Question screen for a specific company in the center.
     */
    public void showAddQuestionScreen(String companyName) {
        AddQuestionScreen addScreen = new AddQuestionScreen(this, companyName, dbManager);
        rootLayout.setCenter(addScreen.getView());
    }
    
    /**
     * [UPDATED] Shows the Profile screen in the center.
     */
    public void showProfileScreen() {
        // Pass the company list to the DB manager to get the total count
        int totalQuestions = dbManager.getTotalQuestionCount(companyList);
        
        // [MODIFIED] Pass the primaryStage to the ProfileScreen constructor
        ProfileScreen profileScreen = new ProfileScreen(this, primaryStage, totalQuestions);
        rootLayout.setCenter(profileScreen.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

