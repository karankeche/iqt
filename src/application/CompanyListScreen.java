package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class CompanyListScreen {

    private final Main mainApp;
    private final List<String> companies;

    public CompanyListScreen(Main mainApp, List<String> companies) {
        this.mainApp = mainApp;
        this.companies = companies; // Receive the sorted list
    }

    public Parent getView() {
        // Main layout for this screen
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30, 40, 30, 40));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #F8F8F8;");

        Label title = new Label("Select Company");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill: #333333;");

        // --- Grid of Company Buttons ---
        GridPane companyGrid = new GridPane();
        companyGrid.setHgap(20);
        companyGrid.setVgap(20);
        companyGrid.setAlignment(Pos.CENTER);

        int maxColumns = 4;
        int col = 0;
        int row = 0;

        for (String company : companies) {
            Button btn = createCompanyButton(company);
            companyGrid.add(btn, col, row);
            
            col++;
            if (col == maxColumns) {
                col = 0;
                row++;
            }
        }
        
        // --- Column Constraints for Grid ---
        for (int i = 0; i < maxColumns; i++) {
            ColumnConstraints colConstraint = new ColumnConstraints();
            colConstraint.setPercentWidth(100.0 / maxColumns);
            colConstraint.setHgrow(Priority.ALWAYS);
            companyGrid.getColumnConstraints().add(colConstraint);
        }

        // --- Scroll Pane ---
        ScrollPane scrollPane = new ScrollPane(companyGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Style scroll pane to blend in
        scrollPane.setStyle("-fx-background: #F8F8F8; -fx-background-color: #F8F8F8; -fx-border-color: #F8F8F8;");
        
        // Make ScrollPane/Grid fill vertical space
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        layout.getChildren().addAll(title, scrollPane);

        return layout;
    }

    /**
     * Creates a professionally styled button for the company grid.
     * @param company The name of the company.
     * @return A styled Button.
     */
    private Button createCompanyButton(String company) {
        Button btn = new Button(company);
        btn.setMaxWidth(Double.MAX_VALUE); // Fill the grid cell
        btn.setPrefHeight(80);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18)); // Slightly smaller font
        btn.setWrapText(true);

        // Professional Button Styling (White Card Look)
        String baseStyle = "-fx-background-color: #FFFFFF; -fx-text-fill: #333333; -fx-background-radius: 10; -fx-border-color: #DDDDDD; -fx-border-radius: 10; -fx-border-width: 1.5;";
        String shadow = "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);";
        
        // Hover style: darkens border, slightly changes background
        String hoverStyle = "-fx-background-color: #F9F9F9; -fx-text-fill: #333333; -fx-background-radius: 10; -fx-border-color: #BCBCBC; -fx-border-radius: 10; -fx-border-width: 1.5;";

        btn.setStyle(baseStyle + shadow);

        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle + shadow));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle + shadow));
        
        // Action
        btn.setOnAction(e -> mainApp.showAddQuestionScreen(company));
        
        return btn;
    }
}

