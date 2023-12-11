package com.example.musicfolderplayer;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerViewModel extends ViewModel {
    private List<File> musicFiles;
    private int currentFileIndex;

    public List<File> getMusicFiles() {
        if (musicFiles == null) {
            return new ArrayList<>();
        }
        return musicFiles;
    }

    public void setMusicFiles(List<File> musicFiles) {
        this.musicFiles = musicFiles;
    }

    public int getCurrentFileIndex() {
        return currentFileIndex;
    }

    public void setCurrentFileIndex(int currentFileIndex) {
        this.currentFileIndex = currentFileIndex;
    }
}
