package com.dakire;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

public class Son {

    Son() {

    }

    public static synchronized void playSound(final String url) {
        try {
            Clip clip = AudioSystem.getClip();
            ClassLoader classLoader = Son.class.getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            URL url_Classe = classLoader.getResource(url);
            AudioInputStream inputStream = AudioSystem
                    .getAudioInputStream(url_Classe);

            clip.open(inputStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.err.println("Erreur son: " + e.getMessage());
        }
    }
}
