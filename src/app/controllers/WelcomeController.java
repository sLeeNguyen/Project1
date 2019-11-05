package app.controllers;

import app.helpers.HelpScene;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class WelcomeController {
    private static HelpScene hs = HelpScene.getInstance();

    @FXML
    void goBack(MouseEvent event) throws IOException {
        hs.changeScene(event, "/app/views/login2.fxml");
    }

    @FXML
    void goOn(MouseEvent event) throws IOException {
        hs.changeScene(event, "/app/home/Home.fxml");
    }

    // Move window
    @FXML
    void pressed(MouseEvent event) {
        hs.pressed(event);
    }

    @FXML
    void dragged(MouseEvent event) {
        hs.dragged(event);
    }
}
