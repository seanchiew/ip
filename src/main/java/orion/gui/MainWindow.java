package orion.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import orion.Orion;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Orion orion;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image orionImage = new Image(this.getClass().getResourceAsStream("/images/DaOrion.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Orion instance */
    public void setOrion(Orion o) {
        orion = o;

        // Show welcome message once at startup
        dialogContainer.getChildren().add(
                DialogBox.getOrionDialog(orion.getWelcomeMessage(), orionImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Orion's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = orion.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getOrionDialog(response, orionImage)
        );

        userInput.clear();

        if (orion.isExit()) {
            Platform.exit();
        }
    }
}
