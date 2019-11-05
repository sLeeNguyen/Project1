package app.add_word;

import app.helpers.CheckAndAlert;
import app.helpers.HelpScene;
import app.helpers.database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddWordController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();
    private static DatabaseHandler dh = DatabaseHandler.getInstance();
    private static CheckAndAlert ca = CheckAndAlert.getInstance();
    private static Connection conn = dh.getConnection();

    @FXML
    private TextField wordTF;

    @FXML
    private TextField meanTF;

    @FXML
    private TextField ipaTF;

    @FXML
    private TextField suggestTF;

    @FXML
    private Label imageLB;

    @FXML
    private Label audioLB;

    @FXML
    private TextField hashTagTF;

    @FXML
    private ChoiceBox<String> classifyCB;

    @FXML
    private BorderPane rootPane;

    @FXML
    private AnchorPane apView;

    @FXML
    private HBox hboxInfor;

    @FXML
    private ImageView imageView;

    private File fileImg;
    private File fileAudio;
    private Date date;

//  ===================================================================================================

    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        setClassifyCB();
        fileImg = null;
        fileAudio = null;
        date = new Date(System.currentTimeMillis());
    }

    private void setClassifyCB() {
        classifyCB.getItems().addAll("none" ,"V", "N", "Adj", "Vp", "Np", "idioms", "Clause");
        classifyCB.setValue("none");
    }

    @FXML
    void onAdd(ActionEvent event) throws FileNotFoundException {
        String word = wordTF.getText().toLowerCase();
        String mean = meanTF.getText().toLowerCase();
        String ipa = ipaTF.getText();
        String suggest = suggestTF.getText();
        String hashTag = hashTagTF.getText();
        String classify = classifyCB.getValue();

        // check input
        if (ca.isNotValid(word)) {
            ca.alertErrorMessage("Bạn cần nhập từ/cụm từ!");
            return;
        }
        if (ca.isNotValid(mean)) {
            ca.alertErrorMessage("Bạn cần nhập nghĩa của từ/cụm từ!");
            return;
        }
        if (ca.isNotValid(ipa)) {
            ca.alertErrorMessage("Bạn cần nhập phiên âm!");
            return;
        }
        if (ca.isHashtagNotValid(hashTag)) {
            ca.alertErrorMessage("Các hash tag phải bắt đầu bằng '#', viết liền và không được có khoảng trắng. Bao gồm các ký tự số, chữ cái và dấu gạch dưới (có ít nhất một chữ cái hoặc chữ số).)");
            return;
        }

        // add to database
        String sql = "INSERT dbo.Information(word, mean, ipa, suggest, classify, pimage, audio, dateWord) Values(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, word);
            pstm.setString(2, mean);
            pstm.setString(3, ipa);
            pstm.setString(4, suggest);
            pstm.setString(5, classify);
            pstm.setDate(8, date);

            // set audio
            if (fileAudio != null) {
                FileInputStream fis = new FileInputStream(fileAudio);
                pstm.setBinaryStream(7, fis, fileAudio.length());
            }
            else {
                pstm.setBinaryStream(7, null);
            }

            // set pimage
            if (fileImg != null) {
                FileInputStream fis = new FileInputStream(fileImg);
                pstm.setBinaryStream(6, fis, fileImg.length());
            }
            else {
                pstm.setBinaryStream(6, null);
            }

            pstm.executeUpdate();
            ca.alertSuccessMessage("Thêm thành công!");

        } catch (SQLException e) {
            ca.alertErrorMessage("Thêm thất bại! \nError: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onRefresh(ActionEvent event) {
        wordTF.clear();
        meanTF.clear();
        ipaTF.clear();
        suggestTF.clear();
        imageLB.setText("");
        audioLB.setText("");
        classifyCB.setValue("none");
        hashTagTF.clear();
        fileImg = null;
        fileAudio = null;
    }

    @FXML
    void onChooseAudio(MouseEvent event) {
        FileChooser.ExtensionFilter imgFilter = new FileChooser.ExtensionFilter("Images Files", "*.mp3", "*.wav");
        FileChooser fc = new FileChooser();

        fc.setTitle("Chọn file phát âm");
        fc.getExtensionFilters().add(imgFilter);
        fileAudio = fc.showOpenDialog(null);

        if (fileAudio != null) {
            String pathAudio = fileAudio.getPath();
            audioLB.setText(pathAudio);
        }
    }

    @FXML
    void onChooseImage(MouseEvent event) {
        FileChooser.ExtensionFilter imgFilter = new FileChooser.ExtensionFilter("Images Files", "*.jpg", "*.png", "*.jpeg", "*.ico");
        FileChooser fc = new FileChooser();

        fc.setInitialDirectory(new File("E:\\Project1\\src\\images"));
        fc.setTitle("Chọn hình ảnh");
        fc.getExtensionFilters().add(imgFilter);
        fileImg = fc.showOpenDialog(null);

        if (fileImg != null) {
            String pathImage = fileImg.getPath();
            imageLB.setText(pathImage);
        }
    }

    @FXML
    void onShowImage(MouseEvent event) {
        hboxInfor.setVisible(false);
        apView.setVisible(true);
        Image image;

        if (fileImg != null) {
            image = new Image(fileImg.toURI().toString());
        } else {
            InputStream is = getClass().getResourceAsStream("../../images/noImage.png");
            image = new Image(is);
        }
        imageView.setImage(image);
    }

    @FXML
    void onBackToHboxInfor(MouseEvent event) {
        apView.setVisible(false);
        hboxInfor.setVisible(true);
    }

    @FXML
    void onPlayAudio(MouseEvent event) {
        if (fileAudio != null) {
            InputStream music;
            try {
                music = new FileInputStream(fileAudio);
                Player player = new Player(music);
                player.play();
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onRemoveTextInImageLB() {
        imageLB.setText("");
        fileImg = null;
    }

    @FXML
    void onRemoveTextInAudioLB() {
        audioLB.setText("");
        fileAudio = null;
    }

    // Move window
    @FXML
    void pressed(MouseEvent event) {
        hs.pressed(event);
    }

    @FXML
    void dragged(MouseEvent event) {
        hs.dragged(event);
    }
}