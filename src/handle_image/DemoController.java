package handle_image;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DemoController {

    @FXML
    private ImageView imgView;
    @FXML
    private ImageView imgView1;

    @FXML
    private JFXTextArea jfxTextAreaImgBytes;

    private byte[] fileContent;

    @FXML
    void chooseImage(ActionEvent event) throws IOException {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Images Files", "*.jpg", "*.png");
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("D:\\image\\Saved Pictures"));
        fc.setTitle("Chọn hình ảnh");
        fc.getExtensionFilters().add(extensionFilter);
        File file = fc.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString(), 284, 200, false, true);
            imgView.setImage(image);

            File fi = new File(file.getPath());
            fileContent = Files.readAllBytes(fi.toPath());
            jfxTextAreaImgBytes.setText(fileContent.toString());
        }
    }

    @FXML
    void convertToImage(ActionEvent event) throws IOException {
        Image img = new Image(new ByteArrayInputStream(fileContent));
        imgView1.setImage(img);
    }
}
