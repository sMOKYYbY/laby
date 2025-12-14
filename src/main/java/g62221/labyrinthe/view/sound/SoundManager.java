package g62221.labyrinthe.view.sound;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {
    private MediaPlayer musicPlayer;
    private AudioClip clickSound;
    private AudioClip slideSound;
    private boolean isMuted = false;

    public SoundManager() {
        try {
            // Chargement de la musique (MediaPlayer pour les fichiers longs/boucle)
            URL musicUrl = getClass().getResource("/sounds/music.mp3");
            if (musicUrl != null) {
                Media media = new Media(musicUrl.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Boucle infinie
                musicPlayer.setVolume(0.3); // Volume musique doux (30%)
                musicPlayer.play();
            }

        } catch (Exception e) {
            System.err.println("Erreur de chargement audio : " + e.getMessage());
        }
    }

    public void playClick() {
        if (!isMuted && clickSound != null) clickSound.play();
    }

    public void playSlide() {
        if (!isMuted && slideSound != null) slideSound.play();
    }
    public void stopSlide() {
        if (slideSound != null) {
            slideSound.stop();
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (musicPlayer != null) {
            musicPlayer.setMute(isMuted);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }
    public void setMusicVolume(double volume) {
        if (musicPlayer != null) {
            musicPlayer.setVolume(volume);
        }
    }

    /**
     * Récupère le volume actuel (utile pour initialiser le slider).
     */
    public double getMusicVolume() {
        return musicPlayer != null ? musicPlayer.getVolume() : 0.3;
    }
}