import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class First extends Application {

    @Override
    public void start(Stage primaryStage) {
        // === Title Section ===
        Text welcomeTitle = new Text("Welcome to Sudoku Galaxy");
        welcomeTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        welcomeTitle.setFill(Color.web("#FFCCFF"));

        // === Game Mode Boxes ===
        VBox dailyBox = createGameBox("Daily Challenge", "file:assets/images/daily.jpg", "Play", "#FF6F61", Color.WHITE);
        VBox classicBox = createGameBox("Classic Sudoku", "file:assets/images/classic.jpg", "Play", "#6C63FF", Color.WHITE);

        HBox topRow = new HBox(50, dailyBox, classicBox);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(20, 0, 10, 0));

        // === Center Section ===
        Text centerTitle = new Text("Classic Sudoku");
        centerTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        centerTitle.setFill(Color.web("#FFFFFF"));

        Button howToPlayBtn = new Button("How to Play");
        howToPlayBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 10;");
        addHoverEffect(howToPlayBtn, "#444", "#666");

        Text howToPlayText = new Text("Click the empty cells and fill in the numbers 1–9.\n" +
                "Make sure each row, column, and 3×3 grid contains all digits without repeating.");
        howToPlayText.setFill(Color.LIGHTGRAY);
        howToPlayText.setFont(Font.font("Arial", 13));
        howToPlayText.setWrappingWidth(500);
        howToPlayText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        howToPlayText.setVisible(false);

        howToPlayBtn.setOnAction(e -> howToPlayText.setVisible(!howToPlayText.isVisible()));

        VBox centerSection = new VBox(20); // tighter grouping
        centerSection.getChildren().addAll(centerTitle, howToPlayBtn, howToPlayText);
        centerSection.setAlignment(Pos.CENTER);
        centerSection.setPadding(new Insets(10, 0, 10, 0));

        // === Bottom Buttons ===
        Button continueBtn = new Button("Continue");
        Button newGameBtn = new Button("New Game");

        styleBottomButton(continueBtn, "#00BFA5");
        styleBottomButton(newGameBtn, "#FF4081");

        addHoverEffect(continueBtn, "#00BFA5", "#00D8B4");
        addHoverEffect(newGameBtn, "#FF4081", "#FF5C9E");

        continueBtn.setOnAction(e -> {
            GameState loadedState = SaveLoadManager.loadGame();
            if (loadedState != null) {
                SudokuGame sudokuGame = new SudokuGame(loadedState);
                Stage gameStage = new Stage();
                sudokuGame.start(gameStage);
                primaryStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Saved Game Found");
                alert.setHeaderText(null);
                alert.setContentText("No saved game was found. Please start a new game first.");
                alert.showAndWait();
            }
        });

        newGameBtn.setOnAction(e -> {
            SudokuGame sudokuGame = new SudokuGame();
            Stage gameStage = new Stage();
            sudokuGame.start(gameStage);
            primaryStage.close();
        });

        HBox bottomRow = new HBox(20, continueBtn, newGameBtn);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(5, 0, 0, 0)); // moved up closer

        // === Main Layout ===
        VBox content = new VBox(25, welcomeTitle, topRow, centerSection, bottomRow);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: #1a1a3d;");

        Scene scene = new Scene(content, 900, 650);
        primaryStage.setTitle("Sudoku Game - Home");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private VBox createGameBox(String titleText, String imagePath, String buttonText, String buttonColor, Color textColor) {
        Text title = new Text(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setFill(Color.WHITE);

        Image image = new Image(imagePath, 200, 120, false, true);
        ImageView imageView = new ImageView(image);
        imageView.setSmooth(true);

        Button playBtn = new Button(buttonText);
        playBtn.setPrefWidth(120);
        playBtn.setPrefHeight(30);
        playBtn.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + toHex(textColor) +
                "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10;");
        addHoverEffect(playBtn, buttonColor, "#FFFFFF30");

        VBox container = new VBox(15, title, imageView, playBtn);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: #2e2e55; -fx-background-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 6);");
        return container;
    }

    private void styleBottomButton(Button button, String color) {
        button.setPrefWidth(130);
        button.setPrefHeight(35); // Same height as Play button
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 12;");
    }

    private void addHoverEffect(Button button, String baseColor, String hoverEffect) {
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.4), 10, 0, 0, 0);"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replaceAll("; -fx-effect:.*", "")));
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
