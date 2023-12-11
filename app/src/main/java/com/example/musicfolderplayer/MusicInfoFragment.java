package com.example.musicfolderplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;

public class MusicInfoFragment extends Fragment {

    private MusicPlayerViewModel viewModel;
    private ImageView imgAlbumArt;
    private TextView txtTitle, txtArtist, txtAlbum, txtDuration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_info, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);

        imgAlbumArt = view.findViewById(R.id.imgAlbumArt);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtArtist = view.findViewById(R.id.txtArtist);
        txtAlbum = view.findViewById(R.id.txtAlbum);
        txtDuration = view.findViewById(R.id.txtDuration);

        // Assuming you have a method to retrieve MusicInfo from MediaMetadataRetriever
        updateViews(viewModel.getMusicFiles().get(viewModel.getCurrentFileIndex()));

        return view;
    }

    private void updateViews(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());

        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        // Get album art as Bitmap
        byte[] albumArtBytes = retriever.getEmbeddedPicture();
        Bitmap albumArt = (albumArtBytes != null) ? BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length) : null;

        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Update views with the metadata
        if (albumArt != null) {
            imgAlbumArt.setImageBitmap(albumArt);
        } else {
            imgAlbumArt.setImageResource(R.drawable.default_album_art);
        }
        txtTitle.setText(title != null ? title : "Unknown Title");
        txtArtist.setText(artist != null ? artist : "Unknown Artist");
        txtAlbum.setText(album != null ? album : "Unknown Album");
        txtDuration.setText(duration != null ? formatDuration(Integer.parseInt(duration)) : "Unknown Duration");
    }

    private String formatDuration(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
