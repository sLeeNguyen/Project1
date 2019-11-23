package app.practice;

import app.helpers.CheckAndAlert;
import app.helpers.HelpScene;
import app.helpers.InformationWord;
import app.helpers.database.DatabaseHandler;
import app.practice.component.WordLabel;
import app.practice.help.HelpLevelController;
import app.practice.level.LevelController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.Vector;

public class PracticeController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();
    private static Connection conn = DatabaseHandler.getInstance().getConnection();
    private static CheckAndAlert ca = CheckAndAlert.getInstance();

    // Start AnchorPane
    @FXML
    private HBox views;

    @FXML
    private AnchorPane startAP;

    // Play AnchorPane
    @FXML
    private AnchorPane playAP;

    @FXML
    private FlowPane answerFP;

    @FXML
    private VBox informationDetail;

    @FXML
    private HBox ipaHB;

    @FXML
    private HBox suggestHB;

    @FXML
    private HBox hashtagHB;

    @FXML
    private HBox classifyHB;

    @FXML
    private Label meanTitleLB;

    @FXML
    private Label ipaLB;

    @FXML
    private Label suggestLB;

    @FXML
    private Label hashtagLB;

    @FXML
    private Label classifyLB;

    @FXML
    private Label dateLB;

    @FXML
    private FontAwesomeIconView iconViewDetail;

    @FXML
    private FontAwesomeIconView playAudio;

    @FXML
    private HBox checkHB;

    @FXML
    private HBox correctHB;

    @FXML
    private HBox incorrectHB;

    @FXML
    private ImageView pimage;

    @FXML
    private Label resultLB;

    @FXML
    private ImageView imgSupport;

    // Support Anchor Pane
    @FXML
    private AnchorPane supportAP;

    @FXML
    private Label ipaLB1;

    @FXML
    private Label suggestLB1;

    private LevelController levelController = null;
    private HelpLevelController helpLevelController = null;
    private int level = 1;
    private int helpLevel = 1;
    private boolean visible = false;
    private boolean isSupportVisible = false;
    private Vector<InformationWord> dataPracticeVT;
    private WordLabel[] answerArr;
    private InformationWord currentIW;
    private int currentIndex;

//  ====================================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startAP.setVisible(true);
        playAP.setVisible(false);
        supportAP.setVisible(isSupportVisible);
        informationDetail.setVisible(visible);
        dataPracticeVT = new Vector<>();
        answerArr = null;
        setVisibleHBox(checkHB, correctHB, incorrectHB);
    }

    private WordLabel[] createWordLabelArray(int numOfElements) {
        WordLabel[] wlArr = new WordLabel[numOfElements];
        for (int i = 0; i < numOfElements; i++) {
            wlArr[i] = new WordLabel("");
            wlArr[i].setDisableLabelStyle(true);
        }
        return wlArr;
    }

    private void loadDatabase() {
        String sql = "SELECT TOP 10 word_id, word, mean, ipa, suggest, hashtag, classify, dateWord FROM Information";
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                InformationWord iw = new InformationWord(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getDate(8));
                dataPracticeVT.add(iw);
            }
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            ca.alertErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setVisibleHBox(HBox hb1, HBox hb2, HBox hb3) {
        hb1.setVisible(true);
        hb2.setVisible(false);
        hb3.setVisible(false);
    }

    private void setInforDetail() {
        meanTitleLB.setText(currentIW.getMean());
        ipaLB.setText(currentIW.getIpa());
        suggestLB.setText(currentIW.getSuggest());
        hashtagLB.setText(currentIW.getHashtag());
        classifyLB.setText(currentIW.getClassify());
        dateLB.setText(currentIW.getDate().toString());

        // level
        if (level >= 2) {
            ipaLB.setText("");
        }
        if (level >= 3) {
            suggestLB.setText("");
        }
        if (level >= 5) {
            meanTitleLB.setText("");
        }

        if (level >= 6) {

        }

        // support level
        ipaLB1.setText(currentIW.getIpa());
        suggestLB1.setText(currentIW.getSuggest());
        if (helpLevel >= 2) {

        }
    }
    
    private void setImageView() {
        Image image = null;
        String sql = "SELECT pimage FROM Information WHERE word_id=" + currentIW.getWord_id();
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                InputStream is = rs.getBinaryStream(1);
                if (is != null) {
                    image = new Image(is);
                    is.close();
                } else {
                    image = new Image(new File("src/images/noImage.png").toURI().toString());
                }
            }
            pimage.setImage(image);
            rs.close();
            pstm.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Start AnchorPane
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
    void onChooseSupportLevel(ActionEvent event) throws IOException {
        if (helpLevelController != null) helpLevel = helpLevelController.getLevel();
        views.getChildren().clear();

        FXMLLoader loaderHelpLevel = new FXMLLoader(getClass().getResource("./help/HelpLevel.fxml"));
        Parent parent = loaderHelpLevel.load();
        helpLevelController = loaderHelpLevel.getController();

        views.getChildren().add(parent);
        helpLevelController.setLevel(helpLevel);
    }

    @FXML
    void onStart(ActionEvent event) {
        dataPracticeVT.clear();
        currentIndex = 0;
        if (levelController != null) level = levelController.getLevel();
        if (helpLevelController != null) helpLevel = helpLevelController.getLevel();
        if (level == 1) {
            ipaHB.setDisable(false);
            suggestHB.setDisable(false);
            playAudio.setStyle("-fx-fill: #2d75e8; -fx-font-size: 4em;");
            playAudio.setDisable(false);
        }
        if (level >= 2) {
            ipaHB.setDisable(true);
        }
        if (level >= 3) {
            suggestHB.setDisable(true);
        }
        if (level >= 4) {
            playAudio.setStyle("-fx-fill: #E5E5E5; -fx-font-size: 4em;");
            playAudio.setDisable(true);
        }
        if (level >= 5) {
            meanTitleLB.setDisable(true);
        }

        if (level >= 6) {

        }


        startAP.setVisible(false);
        playAP.setVisible(true);
        loadDatabase();
        onContinue(event);
    }

    // Play AnchorPane
    @FXML
    void onContinue(ActionEvent event) {
        if (isSupportVisible) {
            isSupportVisible = !isSupportVisible;
            supportAP.setVisible(isSupportVisible);
        }
        answerFP.getChildren().clear();
        if (currentIndex >= dataPracticeVT.size()) {
            ca.alertSuccessMessage("Done");
            return;
        }

        currentIW = dataPracticeVT.get(currentIndex++);
        String currentWord = currentIW.getWord();
        int len = currentWord.length();
        answerArr = createWordLabelArray(len);
        for (int i = 0; i < answerArr.length; i++) {
            answerFP.getChildren().add(answerArr[i]);
            FlowPane.setMargin(answerArr[i], new Insets(10));
        }

        final int[] i = {0};
        Scene scene = playAP.getScene();
        scene.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();
            if (key == KeyCode.BACK_SPACE) {
                if (i[0] > 0) {
                    --(i[0]);
                    answerArr[i[0]].setDisableLabelStyle(true);
                    answerArr[i[0]].setText("");
                }
                return;
            }
            if (((i[0]) < len) && (key.isLetterKey() || key.isDigitKey() || key == KeyCode.SPACE)) {
                answerArr[i[0]].setDisableLabelStyle(false);
                answerArr[i[0]].setText(e.getText());
                (i[0])++;
            }
        });

        setVisibleHBox(checkHB, incorrectHB, correctHB);
        informationDetail.setVisible(false);
        iconViewDetail.setRotate(0);
        setInforDetail();
        setImageView();
    }

    @FXML
    void onCheck(ActionEvent event) {
        boolean flag = true;
        String word = currentIW.getWord();

        for (int i = 0; i < answerArr.length; i++) {
            String answer = answerArr[i].getText();
            if (answer == null || answer.isEmpty() || word.charAt(i) != answer.charAt(0)) {
                flag = false;
                answerArr[i].setIncorrect();
            }
        }
        if (flag) {
            setVisibleHBox(correctHB, checkHB, incorrectHB);
        } else {
            setVisibleHBox(incorrectHB, checkHB, correctHB);
            resultLB.setText("Đáp án: " + currentIW.getWord());
        }
    }

    @FXML
    void onSkip(ActionEvent event) {
        onCheck(event);
    }

    @FXML
    void onPlayAudio(MouseEvent event) {
        String sql = "SELECT audio FROM Information WHERE word_id=" + currentIW.getWord_id();
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            InputStream is = null;
            Player player = null;
            if (rs.next()) {
                is = rs.getBinaryStream(1);
                if (is != null){
                    player = new Player(is);
                    player.play();
                }
            }
            if (is != null) is.close();
            if (player != null) player.close();
            rs.close();
            pstm.close();
        } catch (SQLException | JavaLayerException | IOException e) {
            ca.alertErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onReturn(MouseEvent event) {
        startAP.setVisible(true);
        playAP.setVisible(false);
    }

    @FXML
    void onViewDetail(MouseEvent event) {
        visible = !visible;
        informationDetail.setVisible(visible);
        if (visible) {
            iconViewDetail.setRotate(90);
        } else {
            iconViewDetail.setRotate(0);
        }
    }

    @FXML
    void onViewSuggestDetail(MouseEvent event) {
        ca.alertSuccessMessage("Gợi ý: " + suggestLB.getText());
    }

    // Support Anchor Pane
    @FXML
    void onSupport(MouseEvent event) {
        isSupportVisible = !isSupportVisible;
        supportAP.setVisible(isSupportVisible);
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