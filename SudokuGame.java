import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SudokuGame extends Application {

    private GameState loadedGame;
    private static final int SIZE = 9;
    private Button[][] cells = new Button[SIZE][SIZE];
    private int selectedNumber = 0;
    private int mistakes = 0;
    private int secondsElapsed = 0;
    private Text timerText = new Text("00:00");
    private Text mistakesText = new Text("Mistakes: 0/5");

    private final int[][] initialBoard = {
        {5, 3, 0, 0, 7, 0, 9, 0, 2},
        {6, 0, 0, 1, 9, 5, 0, 4, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {3, 0, 5, 0, 8, 0, 0, 7, 9}
    };

    private final int[][] solutionBoard = {
        {5, 3, 4, 6, 7, 8, 9, 1, 2},
        {6, 7, 2, 1, 9, 5, 3, 4, 8},
        {1, 9, 8, 3, 4, 2, 5, 6, 7},
        {8, 5, 9, 7, 6, 1, 4, 2, 3},
        {4, 2, 6, 8, 5, 3, 7, 9, 1},
        {7, 1, 3, 9, 2, 4, 8, 5, 6},
        {9, 6, 1, 5, 3, 7, 2, 8, 4},
        {2, 8, 7, 4, 1, 9, 6, 3, 5},
        {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    public SudokuGame() {}

    public SudokuGame(GameState loadedGame) {
        this.loadedGame = loadedGame;
    }

    private GameState getCurrentGameState() {
        int[][] currentBoard = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                currentBoard[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
        return new GameState(currentBoard, mistakes, secondsElapsed);
    }

    @Override
    public void start(Stage primaryStage) {
        SoundManager.playBackgroundMusic();
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: #1a1a3d;");


        // Top Bar
        Button backBtn = new Button("← Back");
        styleTopButton(backBtn);
        backBtn.setOnAction(e -> {
            SoundManager.playButtonPress();
        
            Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
            confirmExit.setTitle("Confirm Exit");
            confirmExit.setHeaderText(null);
            confirmExit.setContentText("Are you sure you want to go back? Your game will be saved.");
        
            Button yesBtn = (Button) confirmExit.getDialogPane().lookupButton(javafx.scene.control.ButtonType.OK);
            yesBtn.setText("Yes");
        
            Button noBtn = (Button) confirmExit.getDialogPane().lookupButton(javafx.scene.control.ButtonType.CANCEL);
            noBtn.setText("No");
        
            confirmExit.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    SoundManager.stopBackgroundMusic();
                    SaveLoadManager.saveGame(getCurrentGameState());
                    try {
                        new First().start(primaryStage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                // If "No", do nothing (stay on current screen)
            });
        });
        

        Text title = new Text("Sudoku Classic");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        title.setFill(Color.web("#FFCCFF"));

        Button restartBtn = new Button("Restart");
        styleTopButton(restartBtn);
        restartBtn.setOnAction(e -> {
            SoundManager.playButtonPress();
            SoundManager.stopBackgroundMusic();
            try {
                GameState stateToRestart = (loadedGame != null) ? loadedGame : new GameState(initialBoard, 0, 0);
                new SudokuGame(stateToRestart).start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        Button solveBtn = new Button("Solve");
        styleTopButton(solveBtn);
        solveBtn.setOnAction(e -> {
            SoundManager.playButtonPress();
            solveSudoku();
        });
        
        HBox titleBar = new HBox(20, backBtn, spacerLeft, title, spacerRight, restartBtn, solveBtn);
                titleBar.setAlignment(Pos.CENTER);
        titleBar.setPadding(new Insets(10));

        // Info Bar
        mistakesText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        mistakesText.setFill(Color.web("#FF8080"));

        
        timerText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        timerText.setFill(Color.LIGHTGREEN);
        HBox infoBar = new HBox(50, mistakesText, timerText);
        infoBar.setAlignment(Pos.CENTER);
        infoBar.setPadding(new Insets(10));

        // Load values from saved game if available
        int[][] boardToUse = initialBoard;
        if (loadedGame != null) {
            boardToUse = loadedGame.getCurrentBoard();
            mistakes = loadedGame.getMistakes();
            secondsElapsed = loadedGame.getSecondsElapsed();
            mistakesText.setText("Mistakes: " + mistakes + "/5");
            timerText.setText(String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60));
        }

        startTimer();

        // Grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button cell = new Button();
                cell.setPrefSize(45, 45);
                cell.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                int cellValue = boardToUse[row][col];

                BorderStrokeStyle style = BorderStrokeStyle.SOLID;
                BorderWidths borderWidths = new BorderWidths(
                    row % 3 == 0 ? 3 : 1,
                    col % 3 == 2 ? 3 : 1,
                    row == 8 ? 3 : 1,
                    col % 3 == 0 ? 3 : 1
                );
                cell.setBorder(new Border(new BorderStroke(Color.GRAY, style, CornerRadii.EMPTY, borderWidths)));

                if (initialBoard[row][col] != 0) {
                    cell.setText(String.valueOf(initialBoard[row][col]));
                    cell.setDisable(true);
                    cell.setStyle("-fx-background-color: #44475A; -fx-text-fill: white;");
                } else if (cellValue != 0) {
                    cell.setText(String.valueOf(cellValue));
                    if (cellValue == solutionBoard[row][col]) {
                        cell.setStyle("-fx-background-color: #3CB371; -fx-text-fill: white;"); // Correct
                    } else {
                        cell.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;"); // Wrong
                    }
                }
                

                final int r = row, c = col;
                if (!cell.isDisabled()) {
                    cell.setOnAction(e -> {
                        if (selectedNumber != 0 && selectedNumber != -1 && cell.getText().isEmpty()) {
                            int correct = solutionBoard[r][c];
                            if (selectedNumber == correct) {
                                cell.setText(String.valueOf(selectedNumber));
                                SoundManager.playCorrect();
                                cell.setStyle("-fx-background-color: #3CB371; -fx-text-fill: white;");
                                if (isBoardFilled()) {
                                    SoundManager.stopBackgroundMusic();
                                    SoundManager.playComplete();
                                    showAlert("Congratulations!", "You solved the puzzle!");
                                }
                            } else {
                                mistakes++;
                                SoundManager.playWrong();
                                cell.setText(String.valueOf(selectedNumber));
                                cell.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;");
                                mistakesText.setText("Mistakes: " + mistakes + "/5");
                                if (mistakes >= 5) {
                                    SoundManager.stopBackgroundMusic();
                                    SoundManager.playGameOver();
                                    showAlert("Game Over", "You've made 5 mistakes!");
                                    disableAllInputs();
                                }
                            }
                        } else if (selectedNumber == -1) {
                            cell.setText("");
                            cell.setStyle("-fx-background-color: #1a1a3d; -fx-text-fill: white;");
                        }
                    });
                }

                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        // Number Buttons & Eraser
        HBox numberButtons = new HBox(10);
        numberButtons.setAlignment(Pos.CENTER);
        numberButtons.setPadding(new Insets(10, 0, 0, 0));

        List<Button> selectionButtons = new ArrayList<>();
        String defaultStyle = "-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 10;";
        String selectedStyle = "-fx-background-color: #FFD700; -fx-text-fill: black; -fx-background-radius: 10;";
        String hoverStyle = "-fx-background-color: #7A70FF; -fx-text-fill: white; -fx-background-radius: 10;";

        for (int i = 1; i <= SIZE; i++) {
            Button numBtn = new Button(String.valueOf(i));
            numBtn.setPrefSize(45, 45);
            numBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            numBtn.setStyle(defaultStyle);
            final int number = i;
            numBtn.setOnAction(e -> {
                selectedNumber = number;
                SoundManager.playButtonPress();
                selectionButtons.forEach(b -> b.setStyle(defaultStyle));
                numBtn.setStyle(selectedStyle);
            });
            numBtn.setOnMouseEntered(e -> {
                if (selectedNumber != number) numBtn.setStyle(hoverStyle);
            });
            numBtn.setOnMouseExited(e -> {
                if (selectedNumber != number) numBtn.setStyle(defaultStyle);
            });
            selectionButtons.add(numBtn);
            numberButtons.getChildren().add(numBtn);
        }

        Image eraserImage = new Image("file:assets/erase.png");
        ImageView eraserImageView = new ImageView(eraserImage);
        eraserImageView.setFitWidth(35);
        eraserImageView.setFitHeight(35);
        Button eraseBtn = new Button();
        eraseBtn.setGraphic(eraserImageView);
        eraseBtn.setStyle("-fx-background-color: #FF4C4C; -fx-background-radius: 10;");
        eraseBtn.setPrefSize(45, 45);
        eraseBtn.setOnAction(e -> {
            SoundManager.playButtonPress();
            selectedNumber = -1;
            selectionButtons.forEach(b -> b.setStyle(defaultStyle));
            eraseBtn.setStyle(selectedStyle);
        });
        eraseBtn.setOnMouseEntered(e -> {
            if (selectedNumber != -1) eraseBtn.setStyle("-fx-background-color: #FF6B6B; -fx-background-radius: 10;");
        });
        eraseBtn.setOnMouseExited(e -> {
            if (selectedNumber != -1) eraseBtn.setStyle("-fx-background-color: #FF4C4C; -fx-background-radius: 10;");
        });

        selectionButtons.add(eraseBtn);
        numberButtons.getChildren().add(eraseBtn);

        // Final Layout
        root.getChildren().addAll(titleBar, infoBar, grid, numberButtons);
        Scene scene = new Scene(root, 850, 750);
        primaryStage.setTitle("Sudoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void styleTopButton(Button button) {
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
    }

    private void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            timerText.setText(String.format("%02d:%02d", minutes, seconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void disableAllInputs() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setDisable(true);
            }
        }
    }

    private boolean isBoardFilled() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col].getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 
    private boolean solveSudoku() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col].getText().isEmpty()) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValidMove(row, col, num)) {
                            cells[row][col].setText(String.valueOf(num));
                            cells[row][col].setStyle("-fx-background-color: #3CB371; -fx-text-fill: white;");
    
                            if (solveSudoku()) {
                                return true; // solved!
                            } else {
                                cells[row][col].setText(""); // undo move (backtrack)
                                cells[row][col].setStyle("-fx-background-color: #1a1a3d; -fx-text-fill: white;");
                            }
                        }
                    }
                    return false; // no valid number found
                }
            }
        }
        return true; // all cells filled
    }
    private boolean isValidMove(int row, int col, int number) {
        // Check row
        for (int c = 0; c < SIZE; c++) {
            if (!cells[row][c].getText().isEmpty() && Integer.parseInt(cells[row][c].getText()) == number) {
                return false;
            }
        }
    
        // Check column
        for (int r = 0; r < SIZE; r++) {
            if (!cells[r][col].getText().isEmpty() && Integer.parseInt(cells[r][col].getText()) == number) {
                return false;
            }
        }
    
        // Check 3x3 box
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
    
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (!cells[r][c].getText().isEmpty() && Integer.parseInt(cells[r][c].getText()) == number) {
                    return false;
                }
            }
        }
    
        return true;
    }
        



    public static void main(String[] args) {
        launch(args);
    }
}
