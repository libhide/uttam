package com.ratik.uttam.network;

import android.content.Context;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.model.PhotoType;

import java.io.File;

import javax.inject.Inject;

public class FileProvider {
    public static final String TAG = FileProvider.class.getSimpleName();
    private final Context context;

    @Inject
    public FileProvider(Context context) {
        this.context = context;
    }

    /**
     * @param photoType filename type ("FULL", "REGULAR" or "THUMB")
     * @return absolute path to created file
     */
    public File createFile(PhotoType photoType) {
        File directory;

        directory = context.getDir("Uttam", Context.MODE_PRIVATE);
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e(TAG, "Error creating directory " + directory);
        }

        switch (photoType) {
            case FULL:
                return new File(directory, Constants.General.WALLPAPER_FILE_NAME);
            case REGULAR:
                return new File(directory, Constants.General.WALLPAPER_REGULAR_FILE_NAME);
            case THUMB:
                return new File(directory, Constants.General.WALLPAPER_THUMB_FILE_NAME);
            default:
                return new File(directory, Constants.General.WALLPAPER_FILE_NAME);
        }
    }
}
