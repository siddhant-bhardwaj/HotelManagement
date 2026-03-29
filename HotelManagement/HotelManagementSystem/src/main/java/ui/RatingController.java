package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.animation.RotateTransition;
import javafx.util.Duration;

public class RatingController {

    @FXML private Label star1, star2, star3, star4, star5;
    @FXML private TextArea feedbackArea;

    private int rating = 0;

    @FXML
    private void handleStar(MouseEvent event) {
        Label clicked = (Label) event.getSource();

        if (clicked == star1) rating = 1;
        else if (clicked == star2) rating = 2;
        else if (clicked == star3) rating = 3;
        else if (clicked == star4) rating = 4;
        else if (clicked == star5) rating = 5;

        updateStars();
        animateStar(clicked);
    }

    private void updateStars() {
        Label[] stars = {star1, star2, star3, star4, star5};

        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("★");
                stars[i].getStyleClass().add("selected");
            } else {
                stars[i].setText("☆");
                stars[i].getStyleClass().remove("selected");
            }
        }
    }

    private void animateStar(Label star) {
        RotateTransition rt = new RotateTransition(Duration.millis(300), star);
        rt.setByAngle(360);
        rt.play();
    }

    @FXML
    private void handleSubmit() {
        System.out.println("Rating: " + rating);
        System.out.println("Feedback: " + feedbackArea.getText());

        // Close popup
        Stage stage = (Stage) star1.getScene().getWindow();
        stage.close();
    }
}
