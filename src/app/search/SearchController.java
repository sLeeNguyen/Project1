package app.search;

import app.helpers.CheckAndAlert;
import app.helpers.HelpScene;
import app.helpers.InformationWord;
import app.helpers.database.DatabaseHandler;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();
    private static Connection conn = DatabaseHandler.getInstance().getConnection();
    private static CheckAndAlert ca = CheckAndAlert.getInstance();

    @FXML
    private JFXTextField wordsJFX;

    @FXML
    private JFXTextField hashTagJFX;

    @FXML
    private ChoiceBox<String> classifyCB;

    @FXML
    private DatePicker dateDP;

    @FXML
    private VBox listItemVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setClassifyCB();
    }

    private void setClassifyCB() {
        classifyCB.getItems().addAll("none" ,"V", "N", "Adj", "Vp", "Np", "idioms", "Clause");
        classifyCB.setValue("none");
    }

    @FXML
    void dragged(MouseEvent event) {
        hs.dragged(event);
    }

    @FXML
    void pressed(MouseEvent event) {
        hs.pressed(event);
    }

    private String getSQL() {
        String word = wordsJFX.getText();
        String hashTag = hashTagJFX.getText();
        String classify = classifyCB.getValue();
        LocalDate localDate = dateDP.getValue();
        boolean flag = false;

        String sql = "SELECT word_id, word, mean, ipa, suggest, hashtag, classify, dateWord FROM Information WHERE";

        if (!word.isEmpty()) {
            sql += " word=\'" + word.toLowerCase() + "\' AND";
            flag = true;
        }
        if (!hashTag.isEmpty()) {
            sql += " hashtag=\'" + hashTagJFX.getText() + "\' AND";
            flag = true;
        }
        if (!"none".equals(classify)) {
            sql += " classify=\'" + classify + "\' AND";
            flag = true;
        }
        if (localDate != null) {
            sql += " dateWord=\'" + ((TextField)dateDP.getEditor()).getText()+ "\' AND";
            flag = true;
        }

        if (flag) {
            sql = sql.substring(0, sql.length()-3);
        } else {
            sql = sql.substring(0, sql.length()-6);
        }

        return sql;
    }

    @FXML
    void search(ActionEvent event) {
        listItemVBox.getChildren().clear();

        String sql = getSQL();
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while(rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/search/Item.fxml"));
                Node node = loader.load();
                ItemController itemController = loader.getController();
                InformationWord iw = new InformationWord();

                iw.setWord_id(rs.getInt(1));
                iw.setWord(rs.getString(2));
                iw.setMean(rs.getString(3));
                iw.setIpa(rs.getString(4));
                iw.setSuggest(rs.getString(5));
                iw.setHashtag(rs.getString(6));
                iw.setClassify(rs.getString(7));
                iw.setDate(rs.getDate(8));
                itemController.setIw(iw);

                listItemVBox.getChildren().add(node);
            }
            rs.close();

        } catch (SQLException | IOException e) {
            ca.alertErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onBack(MouseEvent event) throws IOException {
        hs.changeScene(event, "/app/home/Home.fxml");
    }
}