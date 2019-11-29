package app.helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class CheckAndAlert {
    private static CheckAndAlert ca = null;
    // validation
    public boolean isNotValid(String str) {
        if (str == null) return true;
        return !str.matches(".*\\w.*");
    }

    public boolean isHashtagNotValid(String str) {
        if (str == null || str.isEmpty()) return false;
        return !str.matches("(#(_*[a-zA-Z0-9]+_*)+)(\\s+(#(_*[a-zA-Z0-9]+_*)+))*");
    }

    // alert
    public void alertErrorMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void alertSuccessMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public boolean alertConfirmMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    // get
    public static CheckAndAlert getInstance() {
        if (ca == null) {
            ca = new CheckAndAlert();
        }
        return ca;
    }
}
