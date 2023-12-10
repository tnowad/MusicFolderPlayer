package com.example.musicfolderplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    AppCompatButton backButton;
    AppCompatButton exitButton;
    AppCompatButton playButton;
    AppCompatTextView folderPathTextView;
    private RecyclerView listFoldersRecyclerView;
    private FileAdapter fileAdapter;
    private File currentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                ContextCompat.startActivity(this, intent, null);
            }
        }

        listFoldersRecyclerView = findViewById(R.id.recyclerViewListFolders);
        listFoldersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.buttonBackParentFolder);
        playButton = findViewById(R.id.buttonPlayFolder);
        exitButton = findViewById(R.id.buttonExit);
        folderPathTextView = findViewById(R.id.textViewFolderPath);

        backButton.setOnClickListener(v -> {
            try {
                if (currentFolder == null || currentFolder.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                    Toast.makeText(this, "Can't go back to the parent folder", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentFolder = currentFolder.getParentFile();
                displayFiles(currentFolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        playButton.setOnClickListener(v -> {
            try {
                playAllFilesInFolder(currentFolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        exitButton.setOnClickListener(v -> finish());

        try {
            currentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            displayFiles(currentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayFiles(File folder) {
        try {
            if (folder == null || !folder.exists()) {
                return;
            }
            folderPathTextView.setText(folder.getPath());

            File[] files = folder.listFiles();
            if (files != null) {
                fileAdapter = new FileAdapter(files, file -> {
                    if (file.isDirectory()) {
                        currentFolder = file;
                        displayFiles(file);
                    } else {
                        playFiles(new File[]{file});
                    }
                });
                listFoldersRecyclerView.setAdapter(fileAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playAllFilesInFolder(File folder) {
        try {
            if (folder != null && folder.exists()) {
                ArrayList<File> files = new ArrayList<>();
                for (File file : folder.listFiles()) {
                    if (file.isFile() && isMusicFile(file)) {
                        files.add(file);
                    }
                }
                if (files.size() == 0) {
                    Toast.makeText(this, "No music files found in the folder", Toast.LENGTH_SHORT).show();
                    return;
                }
                playFiles(files.toArray(new File[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playFiles(File[] files) {
        try {
            if (files != null && files.length > 0) {
                Intent intent = new Intent(this, MusicPlayerActivity.class);
                intent.putExtra("music_files", files);
                startActivity(intent);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMusicFile(File file) {
        String fileName = file.getName();
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav");
    }
}
