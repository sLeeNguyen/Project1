package app.search;

import app.helpers.CheckAndAlert;
import app.helpers.HelpScene;
import app.helpers.InformationWord;
import app.helpers.database.DatabaseHandler;
import app.search_word_detail.WordDetailController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemController implements Initializable {
    private static HelpScene hs = HelpScene.getInstance();
    private static CheckAndAlert ca = CheckAndAlert.getInstance();
    private static Connection conn = DatabaseHandler.getInstance().getConnection();
    private InformationWord iw;

    @FXML
    private Label wordsLB;

    @FXML
    private Label ipaLB;

    @FXML
    private Label meanLB;

    public void setIw(InformationWord iw) {
        this.iw = iw;
        setData();
    }

    public InformationWord getIw() {
        return iw;
    }

    void setData() {
        wordsLB.setText(iw.getWord());
        ipaLB.setText(iw.getIpa());
        meanLB.setText(iw.getMean());
    }

    @FXML
    void onListenAudio(MouseEvent event) {
        String sql = "SELECT audio FROM Information WHERE word_id=" + iw.getWord_id();
        try {
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            InputStream is = null;
            if (rs.next()) {
                is = rs.getBinaryStream(1);
                if (is == null) {
                    ca.alertSuccessMessage("Không có audio! Hãy thêm file.");
                    return;
                }
                Player player = new Player(is);
                player.play();
            }
            is.close();
        } catch (SQLException | JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onViewDetail(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/search_word_detail/WordDetail2.fxml"));
        Parent parent = loader.load();
        WordDetailController wordDetailController = loader.getController();
        wordDetailController.setIw(iw);
        wordDetailController.showData();
        hs.loadWindow(parent);
    }

    @FXML
    void onDelete(MouseEvent event) {
        if (!ca.alertConfirmMessage("Bạn có muốn xóa?")) return;

        String sql = "DELETE FROM Information WHERE word_id=" + iw.getWord_id();
        try {
            PreparedStatement pstm = conn.prepareStatement(sql);
            if (pstm.executeUpdate() > 0) {
                ca.alertSuccessMessage("Xóa thành công!");
            }

        } catch (SQLException e) {
            ca.alertErrorMessage("Xóa thât bại! Hãy thử lại.");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
