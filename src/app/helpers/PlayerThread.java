package app.helpers;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayerThread extends Thread {
    private Player player;

    public PlayerThread() {}

    public PlayerThread(String pathFileMp3) {
        try {
            player = new Player(new FileInputStream(new File(pathFileMp3)));
        } catch (JavaLayerException | FileNotFoundException e) {
            CheckAndAlert.getInstance().alertErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            player.play();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }
}
