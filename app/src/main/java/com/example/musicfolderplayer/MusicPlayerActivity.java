package com.example.musicfolderplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {
    private MediaPlayerManager mediaPlayerManager;
    private MusicPlayerViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);
        if (savedInstanceState == null) {
            mediaPlayerManager = MediaPlayerManager.getInstance(this);
            List<File> musicFiles = Arrays.asList((File[]) getIntent().getSerializableExtra("music_files"));
            viewModel.setMusicFiles(musicFiles);
            viewModel.setCurrentFileIndex(0);
            mediaPlayerManager.loadFile(viewModel.getMusicFiles().get(viewModel.getCurrentFileIndex()));
            mediaPlayerManager.play();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.action_music_control) {
                loadFragment(new MusicControlFragment());
                return true;
            } else if (itemId == R.id.action_music_info) {
                loadFragment(new MusicInfoFragment());
                return true;
            } else if (itemId == R.id.action_playlist) {
                loadFragment(new PlaylistFragment());
                return true;
            }

            return false;
        });

        loadFragment(new MusicControlFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}