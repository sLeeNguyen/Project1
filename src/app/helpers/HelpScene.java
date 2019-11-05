package app.helpers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelpScene {
    private static HelpScene hs = null;
    private double x = 0, y = 0;

    // =============================== CHANGE SCENE ============================================
    public void changeScene(MouseEvent event, String url) throws IOException {
        Parent welcomeRoot = FXMLLoader.load(getClass().getResource(url));
        Scene scene = new Scene(welcomeRoot);

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    public void changeScene(ActionEvent event, String url) throws IOException {
        Parent welcomeRoot = FXMLLoader.load(getClass().getResource(url));
        Scene scene = new Scene(welcomeRoot);

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    // =============================== MOVE SCENE ============================================
    public void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    public void dragged(MouseEvent event) {
        Node node = (Node) event.getSource();

        Stage stage = (Stage) node.getScene().getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    // =============================== LOAD SCENE ============================================
    public void loadWindow(String loc) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadWindow(Parent parent) {
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public void closeWindow(Stage stage) {
        stage.close();
    }

    // get instance
    public static HelpScene getInstance() {
        if (hs == null) {
            hs = new HelpScene();
        }
        return hs;
    }
}
