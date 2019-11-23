package app.practice.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;

public class WordLabel extends Label {
    private final String LABEL_INCORRECT_STYLES = "-fx-text-fill: WHITE; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-background-color: #FF5252; -fx-cursor: hand;";
    private final String LABEL_FREE_STYLES = "-fx-text-fill: WHITE; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-background-color: #0283DF; -fx-cursor: hand;";
    private final String LABEL_DISABLE_STYLES = "-fx-border-radius: 20px; -fx-background-radius: 20px; -fx-border-width: 3px; -fx-border-color: #BCBCBC; -fx-background-color: transparent;";
    private int index;

    public WordLabel(String text) {
        if (text != null) {
            if (!text.isEmpty())
                this.setText(text);
        }
        setFont(Font.font("Arial Rounded MT Bold", 30));
        setPrefSize(65, 60);
        setStyle(LABEL_FREE_STYLES);
        setAlignment(Pos.CENTER);
        initializeLabelListener();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    private void setLabelPressedStyle() {
        //setStyle(LABEL_PRESSED_STYLES);
        setPrefHeight(56);
        setLayoutY(getLayoutY() + 4);
    }

    private void setLabelReleasedStyle() {
        setStyle(LABEL_FREE_STYLES);
        setPrefHeight(60);
        setLayoutY(getLayoutY() - 4);
    }

    private void initializeLabelListener() {
        setOnMousePressed(event -> setLabelPressedStyle());

        setOnMouseReleased(event -> setLabelReleasedStyle());

        setOnMouseEntered(event -> setEffect(new DropShadow()));

        setOnMouseExited(event -> setEffect(null));
    }

    public void setDisableLabelStyle(boolean bool) {
        if (bool) {
            this.setStyle(LABEL_DISABLE_STYLES);
            this.setText("");
            setDisable(true);
        } else {
            this.setStyle(LABEL_FREE_STYLES);
            setDisable(false);
        }
    }

    public void setIncorrect() {
        this.setStyle(LABEL_INCORRECT_STYLES);
    }
}
