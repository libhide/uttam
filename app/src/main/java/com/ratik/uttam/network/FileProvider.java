package com.ratik.uttam.network;

import android.content.Context;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.domain.model.PhotoType;

import java.io.File;

import javax.inject.Inject;

public class FileProvider {
    private static final String TAG = FileProvider.class.getSimpleName();
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

        return new File(directory, getFileName(photoType));
    }

    /**
     * @param photoType filename type ("FULL", "REGULAR" or "THUMB")
     * @return filename for passed in photoType
     */
    private String getFileName(PhotoType photoType) {
        switch (photoType) {
            case FULL:
                return Constants.General.WALLPAPER_FILE_NAME;
            case REGULAR:
                return Constants.General.WALLPAPER_REGULAR_FILE_NAME;
            case THUMB:
                return Constants.General.WALLPAPER_THUMB_FILE_NAME;
            default:
                return Constants.General.WALLPAPER_FILE_NAME;
        }
    }
}
