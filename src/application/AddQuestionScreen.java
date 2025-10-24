package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
// Import TableView classes
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback; // <-- Import for Cell Factory

import java.util.List;
import java.util.Optional; // <-- Import for Dialogs

public class AddQuestionScreen {

    private final Main mainApp;
    private final String companyName;
    private final DatabaseManager dbManager;

    // UI Components for the form
    private TextArea questionArea;
    private ComboBox<String> difficultyComboBox;
    private ComboBox<String> typeComboBox;
    private Label statusMessageLabel; // For "Success" or "Error" messages

    // --- UI Components for the list ---
    private TableView<Question> questionTableView; // <-- With TableView
    private ObservableList<Question> questionObservableList;

    public AddQuestionScreen(Main mainApp, String companyName, DatabaseManager dbManager) {
        this.mainApp = mainApp;
        this.companyName = companyName;
        this.dbManager = dbManager;
    }

    public Parent getView() {
        // --- Main Layout ---
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(30, 40, 30, 40));
        mainLayout.setStyle("-fx-background-color: #F8F8F8;");

        // --- TOP: Title and Back Button ---
        Label title = new Label("Questions for: " + companyName);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setStyle("-fx-text-fill: #333333;");

        Button backButton = new Button("<- Back to Companies");
        backButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-background-radius: 8;");
        backButton.setOnAction(e -> mainApp.showCompanyListScreen());

        HBox topBar = new HBox(20, backButton, title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        mainLayout.setTop(topBar);

        // --- CENTER: List of Existing Questions ---
        VBox questionListContainer = createQuestionTableView(); // <-- With new method
        mainLayout.setCenter(questionListContainer);
        BorderPane.setMargin(questionListContainer, new Insets(30, 0, 30, 0));

        // --- BOTTOM: Add New Question Form ---
        VBox addQuestionForm = createAddQuestionForm();
        mainLayout.setBottom(addQuestionForm);

        // --- Load data from DB ---
        loadQuestions();

        return mainLayout;
    }

    /**
     * Creates the VBox containing the table view for existing questions.
     */
    private VBox createQuestionTableView() {
        Label listTitle = new Label("Existing Questions");
        listTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        listTitle.setStyle("-fx-text-fill: #555555;");

        // --- Create TableView ---
        questionTableView = new TableView<>();
        questionObservableList = FXCollections.observableArrayList();
        questionTableView.setItems(questionObservableList);

        // --- Create Columns ---

        // 1. Question Column
        TableColumn<Question, String> questionCol = new TableColumn<>("Question");
        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        // Adjusted width to make room for the new column
        questionCol.prefWidthProperty().bind(questionTableView.widthProperty().multiply(0.5)); // 50% width
        questionCol.setCellFactory(tc -> {
            TableCell<Question, String> cell = new TableCell<>();
            Label label = new Label();
            label.setWrapText(true);
            label.prefWidthProperty().bind(questionCol.widthProperty().subtract(10)); // Bind to column width minus padding
            cell.setGraphic(label);
            cell.setAlignment(Pos.CENTER_LEFT);
            label.textProperty().bind(cell.itemProperty());
            return cell;
        });


        // 2. Difficulty Column
        TableColumn<Question, String> difficultyCol = new TableColumn<>("Difficulty");
        difficultyCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        difficultyCol.prefWidthProperty().bind(questionTableView.widthProperty().multiply(0.15)); // 15% width

        // 3. Type Column
        TableColumn<Question, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.prefWidthProperty().bind(questionTableView.widthProperty().multiply(0.15)); // 15% width
        
        // 4. [NEW] Actions Column
        TableColumn<Question, Void> actionCol = new TableColumn<>("Actions");
        actionCol.prefWidthProperty().bind(questionTableView.widthProperty().multiply(0.2)); // 20% width

        Callback<TableColumn<Question, Void>, TableCell<Question, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Question, Void> call(final TableColumn<Question, Void> param) {
                final TableCell<Question, Void> cell = new TableCell<>() {

                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox pane = new HBox(10, updateButton, deleteButton);

                    {
                        updateButton.setStyle("-fx-background-color: #FFA000; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
                        deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
                        pane.setAlignment(Pos.CENTER);

                        // --- Delete Button Action ---
                        deleteButton.setOnAction(event -> {
                            Question question = getTableView().getItems().get(getIndex());
                            
                            // Show confirmation dialog
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Delete Question");
                            alert.setHeaderText("Are you sure you want to delete this question?");
                            alert.setContentText(question.getQuestionText());

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                boolean success = dbManager.deleteQuestion(companyName, question.getId());
                                if (success) {
                                    questionObservableList.remove(question); // Remove from UI
                                    showStatusMessage("Question deleted.", false);
                                } else {
                                    showStatusMessage("Error: Could not delete question.", true);
                                }
                            }
                        });

                        // --- Update Button Action ---
                        updateButton.setOnAction(event -> {
                            Question question = getTableView().getItems().get(getIndex());
                            showUpdateDialog(question);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        };

        actionCol.setCellFactory(cellFactory);

        // Add ALL columns to the table
        questionTableView.getColumns().addAll(questionCol, difficultyCol, typeCol, actionCol);
        
        // Make the table fill the available vertical space
        VBox.setVgrow(questionTableView, Priority.ALWAYS);

        // Style the table view
        questionTableView.setStyle("-fx-font-size: 14px; -fx-border-color: #CCCCCC; -fx-border-radius: 5;");
        // Ensure table automatically resizes columns to fit window
        questionTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);


        return new VBox(15, listTitle, questionTableView);
    }
    
    /**
     * Shows a pop-up dialog to update a question.
     */
    private void showUpdateDialog(Question question) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Question");
        dialog.setHeaderText("Editing question for: " + companyName); // This header will be above the content

        // --- Setup Dialog Buttons ---
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // --- Create Form Layout for Dialog ---
        GridPane grid = new GridPane();
        grid.setHgap(15); // Increased horizontal gap
        grid.setVgap(15); // Increased vertical gap
        grid.setPadding(new Insets(20)); // Padding around the grid content

        // --- Create Form Fields ---
        TextArea questionTextInput = new TextArea(question.getQuestionText());
        questionTextInput.setWrapText(true);
        questionTextInput.setPrefRowCount(5); // Set initial rows, will grow with Vgrow
        questionTextInput.setPromptText("Enter the updated question here...");
        questionTextInput.setStyle("-fx-font-size: 14px; -fx-border-color: #BBBBBB; -fx-border-radius: 5;");

        ComboBox<String> difficultyCombo = new ComboBox<>(FXCollections.observableArrayList("Easy", "Medium", "Hard", "Expert"));
        difficultyCombo.setValue(question.getDifficulty());
        difficultyCombo.setPrefWidth(200); // Give combo boxes a sensible preferred width
        difficultyCombo.setStyle("-fx-font-size: 14px;");

        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Technical", "HR", "Behavioral", "Situational"));
        typeCombo.setValue(question.getType());
        typeCombo.setPrefWidth(200); // Give combo boxes a sensible preferred width
        typeCombo.setStyle("-fx-font-size: 14px;");

        // --- Add Labels and Fields to Grid ---
        Label questionLabel = new Label("Question:");
        questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        GridPane.setValignment(questionLabel, VPos.TOP); // Align label to top for TextArea

        Label difficultyLabel = new Label("Difficulty:");
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label typeLabel = new Label("Type:");
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));


        grid.add(questionLabel, 0, 0);
        grid.add(questionTextInput, 1, 0);
        grid.add(difficultyLabel, 0, 1);
        grid.add(difficultyCombo, 1, 1);
        grid.add(typeLabel, 0, 2);
        grid.add(typeCombo, 1, 2);

        // --- Set Column Constraints ---
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER); // Labels column won't grow
        col1.setPrefWidth(100); // Fixed width for labels column
        
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS); // Input fields column will take all remaining width
        grid.getColumnConstraints().addAll(col1, col2);

        // --- Set Row Constraints for vertical growth ---
        // Allow question text area row to grow
        RowConstraints questionRow = new RowConstraints();
        questionRow.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().add(questionRow);


        dialog.getDialogPane().setContent(grid);
        
        // --- Set Dialog Size and appearance ---
        dialog.getDialogPane().setPrefWidth(600); // Wider dialog
        dialog.getDialogPane().setPrefHeight(400); // Taller dialog
        dialog.getDialogPane().setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Request focus on the question text input when dialog opens
        dialog.setOnShown(event -> questionTextInput.requestFocus());

        // --- Handle Dialog Result ---
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == updateButtonType) {
            // --- Validation ---
            String newText = questionTextInput.getText();
            String newDiff = difficultyCombo.getValue();
            String newType = typeCombo.getValue();

            if (newText == null || newText.trim().isEmpty() || newDiff == null || newType == null) {
                // Show a proper error alert in the dialog
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Validation Error");
                errorAlert.setHeaderText("All fields are required.");
                errorAlert.setContentText("Please fill in all fields before updating.");
                errorAlert.showAndWait();
                // Optionally, re-show the dialog or just return. For now, we return.
                return;
            }

            // [NEW] Show confirmation dialog BEFORE updating
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Update");
            confirmationAlert.setHeaderText("Are you sure you want to apply these changes?");
            confirmationAlert.setContentText("This action will permanently update the question in the database.");

            Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
            
            // Only proceed if user confirms "OK"
            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                // --- Update local object ---
                question.setQuestionText(newText);
                question.setDifficulty(newDiff);
                question.setType(newType);
    
                // --- Save to DB ---
                boolean success = dbManager.updateQuestion(companyName, question);
    
                if (success) {
                    questionTableView.refresh(); // Refresh the table row to show new data
                    showStatusMessage("Question updated successfully.", false);
                } else {
                    showStatusMessage("Error: Could not update question.", true);
                }
            }
            // else: User clicked Cancel on the confirmation, so do nothing.
        }
    }


    /**
     * Creates the VBox containing the form to add a new question.
     */
    private VBox createAddQuestionForm() {
        Label formTitle = new Label("Add a New Question");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        formTitle.setStyle("-fx-text-fill: #555555;");

        // Question Text Area
        questionArea = new TextArea();
        questionArea.setPromptText("Type the interview question here...");
        questionArea.setWrapText(true);
        questionArea.setPrefHeight(100);
        questionArea.setStyle("-fx-font-size: 14px; -fx-border-color: #CCCCCC; -fx-border-radius: 5;");

        // Difficulty ComboBox
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.setItems(FXCollections.observableArrayList("Easy", "Medium", "Hard", "Expert"));
        difficultyComboBox.setPromptText("Select Difficulty");
        difficultyComboBox.setPrefWidth(200);

        // Type ComboBox
        typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList("Technical", "HR", "Behavioral", "Situational"));
        typeComboBox.setPromptText("Select Type");
        typeComboBox.setPrefWidth(200);

        HBox combosBox = new HBox(20, difficultyComboBox, typeComboBox);
        
        // Save Button
        Button saveButton = new Button("Save Question");
        saveButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        saveButton.setPrefHeight(45);
        saveButton.setPrefWidth(200);
        saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        saveButton.setOnAction(e -> saveQuestion());

        // Status Message Label (for success/error)
        statusMessageLabel = new Label();
        statusMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusMessageLabel.setManaged(false); // Hide until needed
        statusMessageLabel.setVisible(false); // Hide until needed
        
        HBox buttonBox = new HBox(30, saveButton, statusMessageLabel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        // Form Layout
        VBox formLayout = new VBox(20, formTitle, questionArea, combosBox, buttonBox);
        formLayout.setPadding(new Insets(30));
        formLayout.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #DDDDDD; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        return formLayout;
    }

    /**
     * Loads questions from the database and populates the TableView.
     */
    private void loadQuestions() {
        List<Question> questions = dbManager.getQuestions(companyName);
        questionObservableList.setAll(questions);
    }

    /**
     * Handles the "Save Question" button click.
     * Validates input, saves to DB, and updates the UI.
     */
    private void saveQuestion() {
        String text = questionArea.getText();
        String difficulty = difficultyComboBox.getValue();
        String type = typeComboBox.getValue();

        // --- Validation ---
        if (text == null || text.trim().isEmpty()) {
            showStatusMessage("Error: Question text cannot be empty.", true);
            return;
        }
        if (difficulty == null || difficulty.isEmpty()) {
            showStatusMessage("Error: Please select a difficulty.", true);
            return;
        }
        if (type == null || type.isEmpty()) {
            showStatusMessage("Error: Please select an interview type.", true);
            return;
        }

        // --- Save to DB ---
        // Use the constructor without the ID for new questions
        Question newQuestion = new Question(text, difficulty, type);
        boolean success = dbManager.addQuestion(companyName, newQuestion);

        // --- Update UI ---
        if (success) {
            // [CHANGED] Reload all questions from DB
            // This ensures the new question is displayed with its proper ID from the database
            loadQuestions(); 
            clearForm();
            showStatusMessage("Success: Question saved!", false);
        } else {
            showStatusMessage("Error: Could not save question to database.", true);
        }
    }

    /**
     * Clears the input form fields.
     */
    private void clearForm() {
        questionArea.clear();
        difficultyComboBox.setValue(null);
        typeComboBox.setValue(null);
    }

    /**
     * Shows a status message (like "Success" or "Error") to the user.
     */
    private void showStatusMessage(String message, boolean isError) {
        statusMessageLabel.setText(message);
        if (isError) {
            statusMessageLabel.setTextFill(Color.RED);
        } else {
            statusMessageLabel.setTextFill(Color.GREEN);
        }
        statusMessageLabel.setManaged(true); // Make it take up space
        statusMessageLabel.setVisible(true); // Make it visible
    }
}

