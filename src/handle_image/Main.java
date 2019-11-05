package handle_image;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String str = null;
        System.out.println(str.isEmpty());
        System.out.println(str.matches("(#(_*[a-zA-Z0-9]+_*)+)+"));
    }

}
