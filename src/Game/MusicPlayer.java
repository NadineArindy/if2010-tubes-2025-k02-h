package src.Game;

import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {

    private Clip clip;
    private int pauseFrame = 0;

    public void playLoop(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playOnce(String filePath, double volume) {
        try {
            File file = new File(filePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip oneShot = AudioSystem.getClip();
            oneShot.open(audio);

            setVolume(oneShot, volume);
            oneShot.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pauseFrame = clip.getFramePosition();
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(pauseFrame);
            clip.start();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private void setVolume(double volume) {
        setVolume(this.clip, volume);
    }

    private void setVolume(Clip c, double volume) {
        if (c == null) return;

        if (!c.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            System.out.println("Volume control not supported!");
            return;
        }

        FloatControl gain = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);

        double min = gain.getMinimum(); // biasanya -80
        double max = gain.getMaximum(); // biasanya +6

        // CLAMP volume 0.0 – 1.0
        if (volume < 0.0) volume = 0.0;
        if (volume > 1.0) volume = 1.0;

        // Convert linear volume → decibel range
        double db = min + (volume * (max - min));
        gain.setValue((float) db);
    }
}