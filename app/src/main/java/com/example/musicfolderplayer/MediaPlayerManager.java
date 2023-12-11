package com.example.musicfolderplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;

public class MediaPlayerManager {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private static MediaPlayerManager instance;
    public final MediaPlayer mediaPlayer;
    private final NotificationManager notificationManager;
    private final Context context;
    private final MediaSession mediaSession;
    public File currentFile;

    private MediaPlayerManager(Context context) {
        this.context = context.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mediaSession = new MediaSession(context, "Music Player");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Player Channel", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static synchronized MediaPlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new MediaPlayerManager(context);
        }
        return instance;
    }

    public void loadFile(File file) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            currentFile = file;
        } catch (IOException e) {
            Log.e("MediaPlayerManager", "Error loading file", e);
        }
    }

    public void play() {
        try {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                showNotification();
            }
        } catch (Exception exception) {

        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            cancelNotification();
        }
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            cancelNotification();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e("MediaPlayerManager", "Error stopping playback", e);
            }
        }
    }


    public void release() {
        mediaPlayer.release();
        cancelNotification();
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle("Music Player")
                .setContentText("Playing music")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true);

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public String getFormattedTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getCurrentTime() {
        return getFormattedTime(mediaPlayer.getCurrentPosition());
    }

    public String getTotalTime() {
        return getFormattedTime(mediaPlayer.getDuration());
    }

    public String getCurrentMusicName() {
        return currentFile.getName();
    }

    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public boolean isPause() {
        return !mediaPlayer.isPlaying();
    }


    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }


}
