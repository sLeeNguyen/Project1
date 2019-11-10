package app.helpers;

import java.sql.Date;

public class InformationWord {
    private int word_id;
    private String word;
    private String mean;
    private String ipa;
    private String suggest;
    private String hashtag;
    private String classify;
    private Date date;

    public InformationWord() {}

    public InformationWord(int word_id, String word, String mean, String ipa, String suggest, String hashtag, String classify, Date date) {
        this.word_id = word_id;
        this.word = word;
        this.mean = mean;
        this.ipa = ipa;
        this.suggest = suggest;
        this.hashtag = hashtag;
        this.classify = classify;
        this.date = date;
    }

    public int getWord_id() {
        return word_id;
    }

    public void setWord_id(int word_id) {
        this.word_id = word_id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
