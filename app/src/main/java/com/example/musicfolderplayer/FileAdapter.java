package com.example.musicfolderplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private final File[] fileList;
    private final OnItemClickListener onItemClickListener;

    public FileAdapter(File[] fileList, OnItemClickListener onItemClickListener) {
        this.fileList = fileList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = fileList[position];
        holder.bindData(file, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        if (fileList == null) {
            return 0;
        }
        return fileList.length;
    }

    public interface OnItemClickListener {
        void onItemClicked(File file);
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {

        private final ImageView fileImage;
        private final TextView fileName;
        private final TextView albumName;
        private final TextView songName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileImage = itemView.findViewById(R.id.imageFileIcon);
            fileName = itemView.findViewById(R.id.textViewFileName);
            albumName = itemView.findViewById(R.id.textViewAlbumName);
            songName = itemView.findViewById(R.id.textViewSongName);
        }

        public void bindData(final File file, final OnItemClickListener onItemClickListener) {
            fileName.setText(file.getName());

            if (isMusicFile(file)) {
                displayMusicInfo(file);
            } else {
//                fileImage.setVisibility(View.GONE);
                albumName.setVisibility(View.GONE);
                songName.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> onItemClickListener.onItemClicked(file));
        }

        private boolean isMusicFile(File file) {
            String fileName = file.getName();
            return fileName.endsWith(".mp3") || fileName.endsWith(".wav");
        }

        private void displayMusicInfo(File file) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getAbsolutePath());

            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            byte[] albumArt = retriever.getEmbeddedPicture();

            albumName.setText(album != null ? album : "");
            songName.setText(title != null ? title : "");

            if (albumArt != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                fileImage.setImageBitmap(bitmap);
            } else {
                fileImage.setImageResource(R.drawable.ic_music);
            }

            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
