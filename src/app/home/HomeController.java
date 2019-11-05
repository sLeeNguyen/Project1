package app.home;

import app.helpers.HelpScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    void addWords(ActionEvent event) {
        hs.loadWindow("/app/add_word/Add.fxml");
    }

    @FXML
    void fixOrDelete(ActionEvent event) {

    }

    @FXML
    void search(ActionEvent event) throws IOException {
        hs.changeScene(event, "/app/search/Search.fxml");
    }

    @FXML
    void setting(ActionEvent event) {

    }

    @FXML
    void test(ActionEvent event) throws IOException {
        hs.changeScene(event, "/app/practice/Practice.fxml");
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
