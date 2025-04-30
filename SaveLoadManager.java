import java.io.*;

public class SaveLoadManager {
    
    // Save the current game state to a file
    public static void saveGame(GameState gameState) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savedGame.dat"))) {
            oos.writeObject(gameState);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving the game: " + e.getMessage());
        }
    }

    // Load the game state from a file
    public static GameState loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savedGame.dat"))) {
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading the game: " + e.getMessage());
            return null;
        }
    }
}
