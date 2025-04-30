import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends Application {

    @Override
    public void start(Stage splashStage) {

        // Load logo image
        Image image = new Image("file:assets/images/logo2.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);

        // Rounded corner clipping
        Rectangle clip = new Rectangle(200, 200);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        imageView.setClip(clip);

        // Add a drop shadow effect to image
        imageView.setEffect(new DropShadow(20, Color.web("#FFD700")));

        // Glowing splash text
        Text splashText = new Text("Sharpen Your Mind, One Puzzle at a Time.");
        splashText.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        splashText.setFill(Color.web("#ffffff"));
        splashText.setEffect(new DropShadow(5, Color.web("#00FFFF")));

        // VBox layout
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(imageView, splashText);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #000428, #004e92);" +
            "-fx-padding: 30;"
        );

        Scene scene = new Scene(root, 800, 600);
        splashStage.setScene(scene);
        splashStage.setTitle("Sudoku Galaxy");
        splashStage.show();

        // Animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), splashText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.2), splashText);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> {
            splashStage.close();
            new First().start(new Stage()); // Your main screen
        });

        fadeIn.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
