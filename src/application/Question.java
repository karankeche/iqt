package application;

public class Question {

    // Added id field
    private int id;
    private String questionText;
    private String difficulty;
    private String type;

    /**
     * Constructor for creating a new question (before saving to DB)
     */
    public Question(String questionText, String difficulty, String type) {
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.type = type;
    }

    /**
     * Constructor for questions retrieved from DB (includes id)
     */
    public Question(int id, String questionText, String difficulty, String type) {
        this.id = id;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.type = type;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getType() {
        return type;
    }

    // --- Setters (for updating) ---

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        // Updated toString (used by ListView, good to have)
        return String.format("[%s - %s] %s", difficulty, type, questionText);
    }
}

