package app.practice.help;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelpLevelController {
    private int level = 1;

    @FXML
    private JFXButton jfxBtnLevel1;

    @FXML
    private JFXButton jfxBtnLevel2;

    @FXML
    private JFXButton jfxBtnLevel3;

    @FXML
    private JFXButton jfxBtnLevel4;

    @FXML
    private Label lbSuccess;

    @FXML
    void onSetLevel(ActionEvent event) {
        // set level
        if (event.getSource() == jfxBtnLevel1) {
            setLevel(1);
        }
        else if (event.getSource() == jfxBtnLevel2) {
            setLevel(2);
        }
        else if (event.getSource() == jfxBtnLevel3) {
            setLevel(3);
        }
        else if (event.getSource() == jfxBtnLevel4) {
            setLevel(4);
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel() {
        this.level = level;
    }

    public void setLevel(int level) {
        this.level = level;
        lbSuccess.setText("Mức hiện tại: "+level);
        lbSuccess.setVisible(true);
    }
}
