import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private Clip backsoundClip;

    /**
     * Memutar musik latar secara terus-menerus (looping).
     */
    public void playBacksound(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.err.println("File backsound tidak ditemukan: " + filePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            backsoundClip = AudioSystem.getClip();
            backsoundClip.open(audioIn);

            // Memutar terus menerus
            backsoundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backsoundClip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Berhenti memutar musik latar.
     */
    public void stopBacksound() {
        if (backsoundClip != null && backsoundClip.isRunning()) {
            backsoundClip.stop();
            backsoundClip.close();
        }
    }

    /**
     * Memutar SFX (suara pendek) satu kali.
     */
    public void playSFX(String filePath) {
        new Thread(() -> {
            try {
                File soundFile = new File(filePath);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                System.err.println("Gagal memutar SFX: " + e.getMessage());
            }
        }).start();
    }
}