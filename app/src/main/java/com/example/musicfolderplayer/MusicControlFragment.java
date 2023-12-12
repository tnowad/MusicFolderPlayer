package com.example.musicfolderplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

public class MusicControlFragment extends Fragment {

    private static final int UPDATE_INTERVAL = 50;
    private Handler handler;
    private MusicPlayerViewModel viewModel;
    private MediaPlayerManager mediaPlayerManager;
    private ImageButton btnPrevious, btnPlayPause, btnNext;
    private ImageView imgAlbumArt;
    private TextView txtElapsedTime, txtTotalTime, txtMusicName;
    private SeekBar seekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music_control, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);
        handler = new Handler(Looper.getMainLooper());

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnNext = view.findViewById(R.id.btnNext);
        txtElapsedTime = view.findViewById(R.id.txtElapsedTime);
        txtTotalTime = view.findViewById(R.id.txtTotalTime);
        txtMusicName = view.findViewById(R.id.txtMusicName);
        seekBar = view.findViewById(R.id.seekBar);
        imgAlbumArt = view.findViewById(R.id.imgAlbumArt);

        btnPrevious.setOnClickListener(v -> onPreviousButtonClick());
        btnPlayPause.setOnClickListener(v -> onPlayPauseButtonClick());
        btnNext.setOnClickListener(v -> onNextButtonClick());

        mediaPlayerManager = MediaPlayerManager.getInstance(view.getContext().getApplicationContext());
        mediaPlayerManager.mediaPlayer.setOnCompletionListener(mp -> {
            onNextButtonClick();
        });
        if (!mediaPlayerManager.isPause()) {
            btnPlayPause.setImageResource(R.drawable.ic_stop);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    mediaPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        updateChangeSong();
        updateSeekBar();
        updateElapsedTime();
        updateSeekBarAndTime();

        return view;
    }

    private void updateChangeSong() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mediaPlayerManager.currentFile.getAbsolutePath());
        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        byte[] albumArtBytes = retriever.getEmbeddedPicture();
        Bitmap albumArt = (albumArtBytes != null) ? BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length) : null;

        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (albumArt != null) {
            imgAlbumArt.setImageBitmap(albumArt);
        } else {
            imgAlbumArt.setImageResource(R.drawable.default_album_art);
        }
        txtMusicName.setText(title != null ? title : "Unknown Title");
    }

    private void onPreviousButtonClick() {
        int currentFileIndex = viewModel.getCurrentFileIndex();
        currentFileIndex--;

        if (currentFileIndex < 0) {
            return;
        }
        viewModel.setCurrentFileIndex(currentFileIndex);
        mediaPlayerManager.loadFile(viewModel.getMusicFiles().get(currentFileIndex));
        updateChangeSong();
        mediaPlayerManager.play();
    }

    private void onPlayPauseButtonClick() {
        if (mediaPlayerManager.isPause()) {
            mediaPlayerManager.play();
            btnPlayPause.setImageResource(R.drawable.ic_stop);
        } else {
            mediaPlayerManager.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void onNextButtonClick() {
        int currentFileIndex = viewModel.getCurrentFileIndex();
        currentFileIndex++;

        if (currentFileIndex >= viewModel.getMusicFiles().size()) {
            return;
        }
        viewModel.setCurrentFileIndex(currentFileIndex);
        mediaPlayerManager.loadFile(viewModel.getMusicFiles().get(currentFileIndex));
        updateChangeSong();
        mediaPlayerManager.play();
    }

    private void updateSeekBarAndTime() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateSeekBar();
                updateElapsedTime();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private void updateSeekBar() {
        int currentPosition = mediaPlayerManager.getCurrentPosition();
        int totalDuration = mediaPlayerManager.getDuration();
        seekBar.setMax(totalDuration);
        seekBar.setProgress(currentPosition);
    }

    private void updateElapsedTime() {
        int elapsedTime = mediaPlayerManager.getCurrentPosition();
        int totalDuration = mediaPlayerManager.getDuration();
        txtElapsedTime.setText(formatTime(elapsedTime));
        txtTotalTime.setText(formatTime(totalDuration));
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
