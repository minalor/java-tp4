import java.io.IOException;
import javax.sound.sampled.*;

public class Son {
    public static synchronized void playSound(final String url) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(Son.class.getResourceAsStream("./SND/" + url));
            clip.open(inputStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.err.println("Erreur son: " + e.getMessage());
        }
    }
}
