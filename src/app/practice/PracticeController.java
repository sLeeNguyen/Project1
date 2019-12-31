package app.practice;

import app.helpers.CheckAndAlert;
import app.helpers.HelpScene;
import app.helpers.InformationWord;
import app.helpers.database.DatabaseHandler;
import app.practice.component.WordLabel;
import app.practice.help.HelpLevelController;
import app.practice.level.LevelController;
import app.practice.mode.ModeController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
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

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PracticeController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();
    private static Connection conn = DatabaseHandler.getInstance().getConnection();
    private static CheckAndAlert ca = CheckAndAlert.getInstance();

    // done
    @FXML
    private VBox doneVB;

    @FXML
    private Label resultDoneLB;

    @FXML
    private Label resultDoneLB1;

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
    private HBox ipaHB1;

    @FXML
    private HBox suggestHB1;

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
    private ModeController modeController = null;
    private int level = 1;
    private int helpLevel = 1;
    private boolean visible = false;
    private boolean isSupportVisible = false;
    private List<InformationWord> dataPracticeVT;
    private List<WordLabel> answerArr;
    private InformationWord currentIW;
    private int currentIndex;
    private int numOfCorrectWords;
    private int numOfWords;

//  ====================================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        meanTitleLB.setText("");
        ipaLB.setText("");
        hashtagLB.setText("");
        suggestLB.setText("");
        classifyLB.setText("");
        dateLB.setText("");
        suggestLB1.setText("");
        ipaLB1.setText("");
        supportAP.setVisible(isSupportVisible);
        informationDetail.setVisible(visible);
        dataPracticeVT = new ArrayList<>();
        answerArr = new ArrayList<>();
        setVisibleElements(checkHB, correctHB, incorrectHB);
        setVisibleElements(startAP, playAP, doneVB);
        numOfCorrectWords = 0;
        numOfWords = 0;
    }

    private List<WordLabel> createWordLabelArray(int numOfElements) {
        List<WordLabel> wlArr = new ArrayList<>();
        WordLabel temp;
        for (int i = 0; i < numOfElements; i++) {
            temp = new WordLabel("");
            temp.setDisableLabelStyle(true);
            wlArr.add(temp);
        }
        return wlArr;
    }

    private boolean loadDatabase() {
        String sql = "SELECT Information.word_id, word, mean, ipa, suggest, hashtag, classify, dateWord, fail FROM Information, Analysis " +
                "WHERE Information.word_id=Analysis.analysisWord_id " +
                "ORDER BY fail DESC";
        if (modeController != null) {
            sql = modeController.getSQL();
        }
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                InformationWord iw = new InformationWord(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
                        rs.getDate(8), rs.getInt(9));
                dataPracticeVT.add(iw);
            }
            rs.close();
            pstm.close();
            numOfWords = dataPracticeVT.size();
            if (dataPracticeVT.isEmpty()) {
                ca.alertSuccessMessage("Không có dữ liệu!");
                setVisibleElements(startAP, playAP, doneVB);
                return false;
            }
        } catch (SQLException e) {
            ca.alertErrorMessage("Không load được dữ liệu. Hãy thử lại hoặc khởi động lại!");
            e.printStackTrace();
        }
        return true;
    }

    private void setVisibleElements(Node node1, Node node2, Node node3) {
        node1.setVisible(true);
        node2.setVisible(false);
        node3.setVisible(false);
    }

    private void setInforDetail() {
        meanTitleLB.setText(currentIW.getMean());
        ipaLB.setText(currentIW.getIpa());
        suggestLB.setText(currentIW.getSuggest());
        hashtagLB.setText(currentIW.getHashtag());
        classifyLB.setText(currentIW.getClassify());
        dateLB.setText(currentIW.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

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
                    image = new Image(new File("E:\\Project1\\src\\app\\images\\noImage.png").toURI().toString());
                }
            }
            pimage.setImage(image);
            rs.close();
            pstm.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Longest common subsequence
    private int[] LCSuff(String s1, String s2) {            // return an array has two elements, values: start position and end position
        int[][] LCS = new int[s1.length()][s2.length()];    // of substring in String 's1'
        int last = 0;
        int maxLen = 0;

        for (int i = 0; i < s1.length(); i++) {
            for (int j = 0; j < s2.length(); j++) {
                LCS[i][j] = 0;
                if (s1.charAt(i) == s2.charAt(j)) {
                    if (i == 0 || j == 0) {
                        LCS[i][j] = 1;
                    } else {
                        LCS[i][j] = LCS[i-1][j-1] + 1;
                    }
                    if (LCS[i][j] > maxLen) {
                        maxLen = LCS[i][j];
                        last = i;
                    }
                }
            }
        }
        return new int[]{last - maxLen + 1, last};
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

        FXMLLoader loaderLevel = new FXMLLoader(getClass().getResource("/app/practice/level/Level.fxml"));
        Parent parent = loaderLevel.load();
        levelController = loaderLevel.getController();

        views.getChildren().add(parent);
        levelController.setLevel(level);
    }

    @FXML
    void onChooseSupportLevel(ActionEvent event) throws IOException {
        if (helpLevelController != null) helpLevel = helpLevelController.getLevel();
        views.getChildren().clear();

        FXMLLoader loaderHelpLevel = new FXMLLoader(getClass().getResource("/app/practice/help/HelpLevel.fxml"));
        Parent parent = loaderHelpLevel.load();
        helpLevelController = loaderHelpLevel.getController();

        views.getChildren().add(parent);
        helpLevelController.setLevel(helpLevel);
    }

    @FXML
    void onChooseMode(ActionEvent event) throws IOException {
        views.getChildren().clear();
        FXMLLoader loaderHelpLevel = new FXMLLoader(getClass().getResource("/app/practice/mode/Mode.fxml"));
        Parent parent = loaderHelpLevel.load();
        modeController = loaderHelpLevel.getController();
        views.getChildren().add(parent);
    }

    @FXML
    void onStart(ActionEvent event) {
        initialize(null, null);
        if (levelController != null) level = levelController.getLevel();
        if (helpLevelController != null) helpLevel = helpLevelController.getLevel();

        dataPracticeVT.clear();
        currentIndex = 0;
        meanTitleLB.setDisable(false);
        ipaHB.setDisable(false);
        suggestHB.setDisable(false);
        playAudio.setStyle("-fx-fill: #2d75e8; -fx-font-size: 4em;");
        playAudio.setDisable(false);
        ipaHB1.setVisible(true);
        suggestHB1.setVisible(true);
        imgSupport.setVisible(true);

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

        /* if level = 6 then no display all WordLabels, handle in onContinue function */


        // support level
        if (helpLevel >= 3) {
            ipaHB1.setVisible(false);
            suggestHB1.setVisible(false);
        }

        if (helpLevel >= 4) {
            imgSupport.setVisible(false);
        }

        setVisibleElements(playAP, startAP, doneVB);
        if (!loadDatabase()) return;
        onContinue(event);
    }

    // Play AnchorPane
    @FXML
    void onContinue(ActionEvent event) {
        answerFP.getChildren().clear();
        answerArr.clear();
        if (isSupportVisible) {
            isSupportVisible = false;
            supportAP.setVisible(false);
        }
        if (currentIndex >= dataPracticeVT.size()) {
            setVisibleElements(doneVB, startAP, playAP);
            resultDoneLB.setText("Đúng: " + numOfCorrectWords + "/" + numOfWords);
            float f = (float) numOfCorrectWords/numOfWords;
            if (f >= 1) {
                resultDoneLB1.setText("Xuất sắc! Hãy tiếp tục giữ vững thành tích.");
            } else if (f >= 0.9) {
                resultDoneLB1.setText("Rất tốt! Hãy tiếp tục cố gắng.");
            } else if (f >= 0.5){
                resultDoneLB1.setText("Hãy cố gắng hơn ở lần tiếp theo.");
            } else {
                resultDoneLB1.setText("Đừng nản! Hãy cố gắng luyện tập.");
            }
            Player player = null;
            try {
                player = new Player(new FileInputStream(new File("E:\\Project1\\src\\app\\audio\\lesson_complete.mp3")));
                player.play();
            } catch (JavaLayerException | FileNotFoundException e) {
                e.printStackTrace();
            }
            if (player != null)
                player.close();
            return;
        }

        currentIW = dataPracticeVT.get(currentIndex++);
        String currentWord = currentIW.getWord();
        int len = currentWord.length();
        Scene scene = playAP.getScene();

        if (level < 6) {
            answerArr = createWordLabelArray(len);
            answerArr.forEach(item -> {
                answerFP.getChildren().add(item);
                FlowPane.setMargin(item, new Insets(10));
            });

            final int[] i = {0};
            scene.setOnKeyPressed(e -> {
                KeyCode key = e.getCode();
                if (key == KeyCode.ENTER) {
                    onCheck(event);
                    return;
                }
                if (key == KeyCode.BACK_SPACE) {
                    if (i[0] > 0) {
                        --(i[0]);
                        answerArr.get(i[0]).setDisableLabelStyle(true);
                        answerArr.get(i[0]).setText("");
                    }
                    return;
                }
                if (((i[0]) < len) && (key.isLetterKey() || key.isDigitKey() || key == KeyCode.SPACE)) {
                    answerArr.get(i[0]).setDisableLabelStyle(false);
                    answerArr.get(i[0]).setText(e.getText());
                    (i[0])++;
                }
            });
        }
        else {  // level == 6
            scene.setOnKeyPressed(e -> {
                KeyCode key = e.getCode();
                if (key == KeyCode.ENTER) {
                    onCheck(event);
                    return;
                }
                if (key == KeyCode.BACK_SPACE) {
                    if (answerArr.size() > 0) {
                        answerFP.getChildren().remove(answerArr.remove(answerArr.size() - 1));
                    }
                    return;
                }
                if ((key.isLetterKey() || key.isDigitKey() || key == KeyCode.SPACE)) {
                    WordLabel item = new WordLabel(e.getText());
                    answerArr.add(item);
                    answerFP.getChildren().add(item);
                    FlowPane.setMargin(item, new Insets(10));
                }
            });
        }

        setVisibleElements(checkHB, incorrectHB, correctHB);
        informationDetail.setVisible(false);
        iconViewDetail.setRotate(0);
        setInforDetail();
        setImageView();
    }

    @FXML
    void onCheck(ActionEvent event) {
        boolean flag = true;
        String word = currentIW.getWord();
        String answer = "";
        for (int i = 0; i < answerArr.size(); i++){
            answer += answerArr.get(i).getText();
        }
        if (answer.isEmpty()) {
            flag = false;
        }
        else {
            if (level < 6) {
                int i = 0;
                for (; i < Math.min(word.length(), answer.length()); i++) {
                    if (answer.charAt(i) != word.charAt(i)) {
                        answerArr.get(i).setIncorrect();
                        flag = false;
                    }
                }
                for (; i < word.length(); i++) {
                    answerArr.get(i).setIncorrect();
                    flag = false;
                }
            }
            else {
                int[] subIndex = LCSuff(answer, word);
                if (answer.length() != word.length() || (subIndex[1]-subIndex[0]+1) != word.length()) {
                    flag = false;
                }
                if (helpLevel >= 2) {
                    for (int i = 0; i < subIndex[0] ; i++){
                        answerArr.get(i).setIncorrect();
                    }
                    for (int i = subIndex[1]+1; i < answerArr.size() ; i++){
                        answerArr.get(i).setIncorrect();
                    }
                }
            }
        }

//        Player player = null;
        if (flag) {
            setVisibleElements(correctHB, checkHB, incorrectHB);
            numOfCorrectWords++;
            currentIW.decreaseNumOfFail();
//                player = new Player(new FileInputStream(new File("audio/right_answer.mp3")));
//                player.play();
        } else {
            setVisibleElements(incorrectHB, checkHB, correctHB);
            resultLB.setText("Đáp án: " + currentIW.getWord());
//                PlayerThread pt = new PlayerThread("audio/wrong_answer.mp3");
//                pt.run();
            currentIW.increaseNumOfFail();
        }

        Scene scene = playAP.getScene();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                onContinue(event);
                return;
            }
        });
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
            ca.alertErrorMessage("Lỗi khi truy nhập audio!");
            e.printStackTrace();
        }
    }

    @FXML
    void onReturn(MouseEvent event) {
        String sql = "UPDATE Analysis SET fail=? WHERE analysisWord_id=?";
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            dataPracticeVT.forEach(element -> {
                try {
                    pstm.setInt(1, element.getNumOfFail());
                    pstm.setInt(2, element.getWord_id());
                    pstm.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            ca.alertErrorMessage("Xảy ra lỗi trong quá trình cập nhật kết quả! Hãy thử khởi động lại.");
            e.printStackTrace();
        }
        setVisibleElements(startAP, playAP, doneVB);
    }

    @FXML
    void onRework(ActionEvent event) {
        currentIndex = 0;
        numOfCorrectWords = 0;
        setVisibleElements(playAP, startAP, doneVB);
        onContinue(event);
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

    @FXML
    void onShowGuide(ActionEvent event) {
        try {
            views.getChildren().clear();
            Parent guide = FXMLLoader.load(getClass().getResource("Guide.fxml"));
            views.getChildren().add(guide);
        } catch (IOException e) {
            ca.alertErrorMessage("Xảy ra lỗi khi load dữ liệu!");
            e.printStackTrace();
        }
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