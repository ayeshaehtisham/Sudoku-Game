import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[][] currentBoard;
    private int mistakes;
    private int secondsElapsed;

    // Constructor
    public GameState(int[][] currentBoard, int mistakes, int secondsElapsed) {
        this.currentBoard = currentBoard;
        this.mistakes = mistakes;
        this.secondsElapsed = secondsElapsed;
    }

    // Getter methods
    public int[][] getCurrentBoard() {
        return currentBoard;
    }

    public int getMistakes() {
        return mistakes;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }
}
