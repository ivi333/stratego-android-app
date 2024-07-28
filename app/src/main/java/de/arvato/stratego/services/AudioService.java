package de.arvato.stratego.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

import de.arvato.stratego.R;

public class AudioService extends Service {

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();
    private boolean isPaused = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.strategosound); // Replace with your audio file
        mediaPlayer.setLooping(true); // Enable looping
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start or resume playback
        startAudio();
        return START_STICKY; // Keep the service running
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudio(); // Stop playback and release resources
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    public void startAudio() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start(); // Start playback
            } else if (isPaused) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                isPaused = false;
            }
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop(); // Stop playback
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }
}