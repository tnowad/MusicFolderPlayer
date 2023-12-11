package com.example.musicfolderplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class PlaylistFragment extends Fragment {

    private MusicPlayerViewModel viewModel;
    private MediaPlayerManager mediaPlayerManager;
    private FileAdapter fileAdapter;
    private RecyclerView playlistRecyclerView;


    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) view.getContext()).get(MusicPlayerViewModel.class);

        mediaPlayerManager = MediaPlayerManager.getInstance(view.getContext());

        playlistRecyclerView = view.findViewById(R.id.recyclerViewPlaylist);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        fileAdapter = new FileAdapter(viewModel.getMusicFiles().toArray(new File[0]), file -> {
            for (int i = 0; i < viewModel.getMusicFiles().size(); i++) {
                if (file.getAbsolutePath().equals(viewModel.getMusicFiles().get(i).getAbsolutePath())) {
                    viewModel.setCurrentFileIndex(i);
                    mediaPlayerManager.loadFile(file);
                    mediaPlayerManager.play();
                    return;
                }
            }
        });

        playlistRecyclerView.setAdapter(fileAdapter);

        return view;
    }

}