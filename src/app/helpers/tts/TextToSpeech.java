package app.helpers.tts;

import app.helpers.CheckAndAlert;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.model.Voices;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import java.io.*;

public class TextToSpeech {
    private static TextToSpeech tts = null;
    private static final String API_KEY = "h7y0x48i4pL8uh_jqJ_LKGEMjn5qkDiuG4esX-RGCf9s";
    private static final String URL = "https://stream.watsonplatform.net/text-to-speech/api";
    private static final String[] listVoices = {"en-US_AllisonV2Voice", "en-US_AllisonVoice" , "en-US_MichaelV3Voice",
            "en-US_LisaV2Voice",  "en-US_MichaelV2Voice",  "en-GB_KateV3Voice", "en-US_LisaVoice", "en-US_MichaelVoice",
            "en-US_AllisonV3Voice", "en-GB_KateVoice"};
    private static CheckAndAlert ca = CheckAndAlert.getInstance();

    // show list voices
    public void list_voices() {
        IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        com.ibm.watson.text_to_speech.v1.TextToSpeech textToSpeech = new com.ibm.watson.text_to_speech.v1.TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(URL);

        Voices voices = textToSpeech.listVoices().execute().getResult();
    }

    public boolean SoundCreator(String text) {
        Authenticator authenticator = new IamAuthenticator("h7y0x48i4pL8uh_jqJ_LKGEMjn5qkDiuG4esX-RGCf9s");
        com.ibm.watson.text_to_speech.v1.TextToSpeech service = new com.ibm.watson.text_to_speech.v1.TextToSpeech(authenticator);

        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                .text(text)
                .voice(listVoices[(int) (Math.random()*10)])
                .accept("audio/mp3")
                .build();

        try {
            InputStream is = service.synthesize(synthesizeOptions).execute().getResult();
            writeToFile(WaveUtils.reWriteWaveHeader(is), new File("audio.mp3"));
            is.close();
            return true;
        } catch (IOException e) {
            ca.alertErrorMessage("Tạo file phát âm không thành công! Hãy đảm bảo bạn đang kết nối Internet, kiểm tra lại đường truyền và khởi động lại.");
            e.printStackTrace();
            return false;
        }
    }

    public void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TextToSpeech getInstance() {
        if (tts == null) {
            tts = new TextToSpeech();
        }
        return tts;
    }
}
