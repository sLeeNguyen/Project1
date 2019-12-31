package app.practice.mode;

import app.helpers.CheckAndAlert;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ModeController implements Initializable {
    private static CheckAndAlert ca;

    @FXML
    private VBox vbMode;

    @FXML
    private JFXTextField hashtagJFXTF;

    @FXML
    private JFXTextField dateJFXTF;

    @FXML
    private JFXTextField monthJFXTF;

    @FXML
    private JFXTextField yearJFXTF;

    @FXML
    private JFXDatePicker fromDateJFXDP;

    @FXML
    private JFXDatePicker toDateJFXDP;

    @FXML
    private ChoiceBox numOfWords;

    public class Mode {
        private String hashTag;
        private int date;
        private int month;
        private int year;
        private LocalDate fromDate;
        private LocalDate toDate;
        private int numOfWords;

        public Mode() {
            this.hashTag = "";
            this.date = -1;
            this.month = -1;
            this.year = -1;
            this.fromDate = null;
            this.toDate = null;
            this.numOfWords = -1;
        }

        public LocalDate getFromDate() {
            return fromDate;
        }

        public void setFromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public void setToDate(LocalDate toDate) {
            this.toDate = toDate;
        }

        public String getHashTag() {
            return hashTag;
        }

        public void setHashTag(String hashtag) {
            this.hashTag = hashtag;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getNumOfWords() {
            return numOfWords;
        }

        public void setNumOfWords(int numOfWords) {
            this.numOfWords = numOfWords;
        }
    }

    private Mode mode;
    /*===========================================================*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ca = CheckAndAlert.getInstance();
        mode = new Mode();
        setClassifyCB();
    }

    public Mode getMode() {
        return mode;
    }

    private void setClassifyCB() {
        numOfWords.getItems().addAll("All", 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);
        numOfWords.setValue("All");
    }

    public String getSQL() {
        String sql;
        boolean changeSQL = false;

        if (mode.numOfWords <= 0) {
            sql = "SELECT Information.word_id, word, mean, ipa, suggest, hashtag, classify, dateWord, fail FROM Information, Analysis " +
                    "WHERE Information.word_id= Analysis.analysisWord_id";
        }
        else {
            sql = "SELECT TOP " + mode.getNumOfWords() + " Information.word_id, word, mean, ipa, suggest, hashtag, classify, dateWord, fail FROM Information, Analysis " +
                    "WHERE Information.word_id= Analysis.analysisWord_id";
        }

        if (!mode.hashTag.isEmpty()) {
            sql += " AND (hashtag LIKE \'%"+mode.getHashTag() +" %\' OR hashtag LIKE \'%" + mode.getHashTag() + "\')";
            changeSQL = true;
        }
        boolean flag = true;
        if (mode.getDate() >= 0) {
            sql += " AND DAY(dateWord)=" + mode.getDate();
            flag = false;
            changeSQL = true;
        }
        if (mode.getMonth() >= 0) {
            sql += " AND MONTH(dateWord)=" + mode.getMonth();
            flag = false;
            changeSQL = true;
        }
        if (mode.getYear() >= 0) {
            sql += " AND YEAR(dateWord)=" + mode.getYear();
            flag = false;
            changeSQL = true;
        }
        if (flag && mode.fromDate != null) {
            sql += " AND dateWord>=\'" + mode.fromDate.toString()+"\'";
            if (mode.toDate != null) {
                sql += " AND dateWord<=\'" + mode.toDate.toString()+"\'";
            }
            changeSQL = true;
        }
        sql += " ORDER BY fail DESC";
        return sql;
    }

    private int checkNumber(String s1, int k) {    // k = 1: Day, k = 2: Month, k = 3: Year
        int value;
        if (s1.isEmpty()) return -2;
        if (!StringUtils.isNumeric(s1)) {
            value = -1;
        }
        else {
            value = Integer.valueOf(s1);
            if (k == 1 && value > 31) {
                value = -1;
            }
            else if (k == 2 && value > 12) {
                value = -1;
            }
        }
        return value;
    }

    private boolean validation() {
        String hashtag = hashtagJFXTF.getText();
        int date = checkNumber(dateJFXTF.getText(), 1);
        int month = checkNumber(monthJFXTF.getText(), 2);
        int year = checkNumber(yearJFXTF.getText(), 3);
        LocalDate fromDate = fromDateJFXDP.getValue();
        LocalDate toDate = toDateJFXDP.getValue();


        if (!hashtag.isEmpty() && ca.isHashtagNotValid(hashtag)) {
            ca.alertErrorMessage("Mỗi hash tag phải bắt đầu bằng '#', viết liền không dấu và cách nhau bằng khoảng trắng giữa các hash tag. Bao gồm các ký tự số, chữ cái và dấu gạch dưới (có ít nhất một chữ cái hoặc chữ số).");
            return false;
        }

        if (date == -1){
            ca.alertErrorMessage("Ngày không hợp lệ!");
            return false;
        }
        if (month == -1) {
            ca.alertErrorMessage("Tháng không hợp lệ!");
            return false;
        }
        if (year == -1) {
            ca.alertErrorMessage("Năm không hợp lệ!");
            return false;
        }

        if (toDate != null) {
            if (fromDate == null) {
                ca.alertErrorMessage("Hãy chọn FromDate!");
                return false;
            }
            if (fromDate.isAfter(toDate)) {
                ca.alertErrorMessage("FromDate phải nhỏ hơn ToDate!");
                return false;
            }
        }
        else {
            if (fromDate != null && fromDate.isAfter(LocalDate.now())) {
                ca.alertErrorMessage("FromDate không hợp lệ!\nHôm nay là " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return false;
            }
        }

        mode.setHashTag(hashtag);
        mode.setYear(year);
        mode.setMonth(month);
        mode.setDate(date);
        mode.setFromDate(fromDate);
        mode.setToDate(toDate);
        if (!numOfWords.getValue().toString().equals("All"))
            mode.setNumOfWords((int)numOfWords.getValue());

        return true;
    }

    @FXML
    void onConfirm(ActionEvent event) {
        if (validation()) {
            ca.alertSuccessMessage("Thiết đặt thành công!");
        }
    }

    @FXML
    void onReset(ActionEvent event) {
        hashtagJFXTF.setText("");
        dateJFXTF.setText("");
        monthJFXTF.setText("");
        yearJFXTF.setText("");
        fromDateJFXDP.setValue(null);
        toDateJFXDP.setValue(null);
        fromDateJFXDP.setDisable(false);
        toDateJFXDP.setDisable(false);
    }

    @FXML
    void onSetDefault(ActionEvent event) {
        onReset(event);
        mode = new Mode();
        ca.alertSuccessMessage("Thiết đặt thành công!");
    }

    @FXML
    void onInputTime(KeyEvent event) {
        if (!dateJFXTF.getText().isEmpty() || !monthJFXTF.getText().isEmpty() || !yearJFXTF.getText().isEmpty()) {
            fromDateJFXDP.setValue(null);
            toDateJFXDP.setValue(null);
            fromDateJFXDP.setDisable(true);
            toDateJFXDP.setDisable(true);
        }
        else {
            fromDateJFXDP.setDisable(false);
            toDateJFXDP.setDisable(false);
        }
    }
}
