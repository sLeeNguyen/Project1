package app.practice;

import app.helpers.HelpScene;
import app.practice.help.HelpLevelController;
import app.practice.level.LevelController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PracticeController {
    private static HelpScene hs = HelpScene.getInstance();
    private LevelController levelController = null;
    private HelpLevelController helpLevelController = null;
    private int level = 1;
    private int helpLevel = 1;

    @FXML
    HBox views;

    @FXML
    ImageView imgGame;

    @FXML
    void onBack(MouseEvent event) throws IOException {
        hs.changeScene(event, "/app/home/Home.fxml");
    }

    @FXML
    void onChooseLevel(ActionEvent event) throws IOException {
        if (levelController != null) level = levelController.getLevel();
        views.getChildren().clear();

        FXMLLoader loaderLevel = new FXMLLoader(getClass().getResource("./level/Level.fxml"));
        Parent parent = loaderLevel.load();
        levelController = loaderLevel.getController();

        views.getChildren().add(parent);
        levelController.setLevel(level);
    }

    @FXML
    void onStart(ActionEvent event) {

    }

    @FXML
    void onSupport(ActionEvent event) throws IOException {
        if (helpLevelController != null) helpLevel = helpLevelController.getLevel();
        views.getChildren().clear();

        FXMLLoader loaderHelpLevel = new FXMLLoader(getClass().getResource("./help/HelpLevel.fxml"));
        Parent parent = loaderHelpLevel.load();
        helpLevelController = loaderHelpLevel.getController();

        views.getChildren().add(parent);
        helpLevelController.setLevel(helpLevel);
    }

//  Move window
    @FXML
    void pressed(MouseEvent event) {
        hs.pressed(event);
    }

    @FXML
    void dragged(MouseEvent event) {
        hs.dragged(event);
    }
}
