import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundManager {

    private static MediaPlayer backgroundMusicPlayer;

    public static void playBackgroundMusic() {
        try {
            Media backgroundMusic = new Media(SoundManager.class.getResource("/assets/songs/bg_music.mpeg").toString());
            backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusicPlayer.setVolume(0.3); // adjust volume if needed
            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.out.println("Background music failed to load.");
        }
    }
    public static void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer = null;
        }
    }
    
    public static void playCorrect() {
        playSound("/assets/songs/success.mp3");
    }

    public static void playWrong() {
        playSound("assets/songs/wrong_ans.mp3");
    }

    public static void playComplete() {
        playSound("/assets/songs/complete.wav");
    }

    public static void playGameOver() {
        playSound("/assets/songs/game_over.mp3");
    }

    public static void playButtonPress() {
        playSound("/assets/songs/button_press.mp3");
    }

    private static void playSound(String filePath) {
        try {
            Media sound = new Media(SoundManager.class.getResource(filePath).toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Sound failed to play: " + filePath);
        }
    }
}
