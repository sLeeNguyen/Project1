package app.search_word_detail;

import app.helpers.CheckAndAlert;
import app.helpers.HelpScene;
import app.helpers.InformationWord;
import app.helpers.database.DatabaseHandler;
import app.helpers.tts.TextToSpeech;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class WordDetailController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();
    private static Connection conn = DatabaseHandler.getInstance().getConnection();
    private static CheckAndAlert ca = CheckAndAlert.getInstance();
    private static Image imageDefault;

    @FXML
    private BorderPane rootPaneUpdate;

    @FXML
    private BorderPane rootPaneDetail;

    @FXML
    private JFXTextField wordsTF;

    @FXML
    private JFXTextField meanTF;

    @FXML
    private JFXTextField ipaTF;

    @FXML
    private JFXTextField suggestTF;

    @FXML
    private JFXTextField hashTagTF;

    @FXML
    private ChoiceBox<String> classifyCB;

    @FXML
    private Label dateLBUpdate;

    @FXML
    private ImageView imgUpdate;

    @FXML
    private Label imageLB;

    @FXML
    private Label audioLB;

//  --------------------------------

    @FXML
    private Label wordLB;

    @FXML
    private Label meanLB;

    @FXML
    private Label ipaLB;

    @FXML
    private Label suggestLB;

    @FXML
    private Label hashtagLB;

    @FXML
    private Label classifyLB;

    @FXML
    private Label dateLBDetail;

    @FXML
    private ImageView imgDetail;

    private InformationWord iw;
    private File fileImg;
    private File fileAudio;
    private boolean isRootPaneDetail;
//  =========================================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileImg = null;
        fileAudio = null;
        try {
            imageDefault = new Image(new FileInputStream("E:\\Project1\\src\\app\\images\\noImage.png"));
        } catch (FileNotFoundException e) {
            ca.alertErrorMessage("Lỗi load ảnh!");
            e.printStackTrace();
        }
        isRootPaneDetail = true;
    }

    public void setIw(InformationWord iw) {
        this.iw = iw;
    }

    public void showData() {
        wordsTF.setText(iw.getWord());
        meanTF.setText(iw.getMean());
        ipaTF.setText(iw.getIpa());
        suggestTF.setText(iw.getSuggest());
        hashTagTF.setText(iw.getHashtag());
        setClassifyCB(iw.getClassify());
        dateLBDetail.setText(iw.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        wordLB.textProperty().bind(wordsTF.textProperty());
        meanLB.textProperty().bind(meanTF.textProperty());
        ipaLB.textProperty().bind(ipaTF.textProperty());
        suggestLB.textProperty().bind(suggestTF.textProperty());
        hashtagLB.textProperty().bind(hashTagTF.textProperty());
        classifyLB.textProperty().bind(classifyCB.valueProperty());
        dateLBUpdate.textProperty().bind(dateLBDetail.textProperty());

        showImage();
    }

    void showImage() {
        String sql = "SELECT pimage FROM Information WHERE word_id=\'" + iw.getWord_id() + "\'";
        Image image1 = imageDefault;
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                InputStream is = rs.getBinaryStream(1);
                if (is != null) {
                    image1 = new Image(is);
                }
            }
            imgDetail.setImage(image1);
            imgUpdate.setImage(image1);

        } catch (SQLException e) {
            ca.alertErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void setBPUpdate() {
        rootPaneUpdate.setVisible(true);
        rootPaneDetail.setVisible(false);
        isRootPaneDetail = false;
    }

    @FXML
    void setBPDetail() {
        rootPaneDetail.setVisible(true);
        rootPaneUpdate.setVisible(false);
        isRootPaneDetail = true;
    }

    @FXML
    void onViewSuggestDetail() {
        ca.alertSuccessMessage("Gợi ý: " + suggestLB.getText());
    }

    private void setClassifyCB(String selected) {
        classifyCB.getItems().addAll("none" ,"V", "N", "Adj", "Vp", "Np", "idioms", "Clause");
        classifyCB.setValue(selected);
    }

    @FXML
    void onUpdate(ActionEvent event) {
        String word = wordsTF.getText().toLowerCase();
        String mean = meanTF.getText().toLowerCase();
        String ipa = ipaTF.getText();
        String suggest = suggestTF.getText();
        String hashTag = hashTagTF.getText();
        String classify = classifyCB.getValue();
        PreparedStatement pstm;
        String sql;

        if (ca.isNotValid(word)) {
            ca.alertErrorMessage("Bạn cần nhập từ/cụm từ!");
            return;
        }
        if (ca.isNotValid(mean)) {
            ca.alertErrorMessage("Bạn cần nhập nghĩa của từ/cụm từ!");
            return;
        }
        if (ca.isNotValid(ipa) && !ipa.isEmpty()) {
            ca.alertErrorMessage("Bạn cần nhập phiên âm!");
            return;
        }

        if (ca.isHashtagNotValid(hashTag)) {
            ca.alertErrorMessage("Các hash tag phải bắt đầu bằng '#', viết liền và không được có khoảng trắng. Bao gồm các ký tự số, chữ cái và dấu gạch dưới (có ít nhất một chữ cái hoặc chữ số).)");
            return;
        }

        try {
            if (fileImg != null) {
                sql = "UPDATE Information SET pimage=? WHERE word_id=\'" + iw.getWord_id() + "\'";
                pstm = conn.prepareStatement(sql);

                FileInputStream fis = new FileInputStream(fileImg);
                pstm.setBinaryStream(1, fis, fileImg.length());
                pstm.executeUpdate();
            }
            if (fileAudio != null) {
                sql = "UPDATE Information SET audio=? WHERE word_id=\'" + iw.getWord_id() + "\'";
                pstm = conn.prepareStatement(sql);

                FileInputStream fis = new FileInputStream(fileAudio);
                pstm.setBinaryStream(1, fis, fileAudio.length());
                pstm.executeUpdate();
            }

            sql = "UPDATE Information SET word=?, mean=?, ipa=?, suggest=?, hashtag=?, classify=? WHERE word_id=" + iw.getWord_id();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, word);
            pstm.setString(2, mean);
            pstm.setString(3, ipa);
            pstm.setString(4, suggest);
            pstm.setString(5, hashTag);
            pstm.setString(6, classify);
//            pstm.setDate(7, date);

            pstm.executeUpdate();
            ca.alertSuccessMessage("Cập nhật thành công!");

        } catch (SQLException | FileNotFoundException e) {
            ca.alertErrorMessage("Cập nhật không thành công!");
            e.printStackTrace();
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) rootPaneUpdate.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onDelete(ActionEvent event) {
        if (!ca.alertConfirmMessage("Bạn có muốn xóa?")) return;

        String sql = "DELETE FROM Information WHERE word_id=" + iw.getWord_id();
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            if (pstm.executeUpdate() > 0) {
                ca.alertSuccessMessage("Xóa thành công!");
            }
            hs.closeWindow((Stage) rootPaneDetail.getScene().getWindow());

        } catch (SQLException e) {
            ca.alertErrorMessage("Xóa thất bại! Hãy thử lại.");
            e.printStackTrace();
        }
    }

    @FXML
    void onPlayAudio(MouseEvent event) throws IOException {
        InputStream is;
        try {
            if (fileAudio == null || isRootPaneDetail) {
                String sql = "SELECT audio FROM Information WHERE word_id=?";
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.setInt(1, iw.getWord_id());
                ResultSet rs = pstm.executeQuery();
                if (rs.next()) {
                    is = rs.getBinaryStream(1);
                    if (is != null) {
                        Player player = new Player(is);
                        player.play();
                    }
                    else {
                        ca.alertSuccessMessage("Không có audio!");
                    }
                }
            } else {
                    is = new FileInputStream(fileAudio);
                    Player player = new Player(is);
                    player.play();
            }

        } catch (JavaLayerException | SQLException | IOException e) {
            ca.alertErrorMessage("Lỗi khi load dữ liệu! Hãy thử khởi động lại.");
            e.printStackTrace();
        }
    }

    @FXML
    void onUseTTS(ActionEvent event) {
        TextToSpeech tts = TextToSpeech.getInstance();
        boolean isSuccess = tts.SoundCreator(wordsTF.getText());
        if (isSuccess) {
            fileAudio = new File("audio.mp3");
            ca.alertSuccessMessage("Tạo audio thành công!");
        }
    }

    @FXML
    void onChooseAudio(MouseEvent event) {
        FileChooser.ExtensionFilter imgFilter = new FileChooser.ExtensionFilter("Images Files", "*.mp3");
        FileChooser fc = new FileChooser();

        fc.setTitle("Chọn file phát âm");
        fc.getExtensionFilters().add(imgFilter);
        fileAudio = fc.showOpenDialog(null);

        if (fileAudio != null) {
            audioLB.setText(fileAudio.toPath().toString());
        }
    }

    @FXML
    void onChooseImage(MouseEvent event) {
        FileChooser.ExtensionFilter imgFilter = new FileChooser.ExtensionFilter("Images Files", "*.jpg", "*.png", "*.jpeg", "*.ico");
        FileChooser fc = new FileChooser();

        fc.setTitle("Chọn hình ảnh");
        fc.getExtensionFilters().add(imgFilter);
        fileImg = fc.showOpenDialog(null);

        if (fileImg != null) {
            imageLB.setText(fileImg.toPath().toString());
            Image image = new Image(fileImg.toURI().toString());
            imgUpdate.setImage(image);
        }
    }

    @FXML
    void onRemoveTextInAudioLB(MouseEvent event) {
        if (!audioLB.getText().isEmpty()) {
            audioLB.setText("");
            fileAudio = null;
        }
    }

    @FXML
    void onRemoveTextInImageLB(MouseEvent event) {
        if (!imageLB.getText().isEmpty()) {
            imageLB.setText("");
            fileImg = null;
            imgUpdate.setImage(imgDetail.getImage());
        }
    }

//  =================== Move window ========================
    @FXML
    void dragged(MouseEvent event) {
        hs.dragged(event);
    }

    @FXML
    void pressed(MouseEvent event) {
        hs.pressed(event);
    }
}
