package app.controllers;

import app.helpers.HelpScene;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoginController {
    private static HelpScene hs = HelpScene.getInstance();

    @FXML
    private JFXTextField jfxUsername;

    @FXML
    private JFXPasswordField jfxPassword;

    @FXML
    void login(ActionEvent event) throws IOException {
        String username = jfxUsername.getText();
        String password = jfxPassword.getText();

        if (username.equals("admin") && password.equals("admin")){
            hs.changeScene(event, "/app/views/Welcome.fxml");
        }
    }

    @FXML
    void toWelcome(MouseEvent event) throws IOException {
        hs.changeScene(event, "/app/views/Welcome.fxml");
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
