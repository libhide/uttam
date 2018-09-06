package com.ratik.uttam.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import io.reactivex.Single;


/**
 * Created by Ratik on 02/02/18.
 */

public class FileHelper {

    public FileHelper() {
        // Not implemented
    }

    /**
     * Takes a source file and copies it to sdcard/Pictures/Uttam
     *
     * @param srcFile              source file
     * @param exportedFileFilename filename for exported file
     * @return Observable of the exported file
     */
    public Single<File> exportFile(File srcFile, String exportedFileFilename) throws IOException {
        File destination = getExtStorageDir();
        if (!destination.exists() && !destination.mkdirs()) {
            Throwable t = new Throwable("Error making directory: " + destination.getPath());
            return Single.error(t);
        }

        File exportedFile = new File(destination, exportedFileFilename);
        if (exportedFile.exists()) {
            return Single.just(exportedFile);
        }

        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(srcFile).getChannel();
            outChannel = new FileOutputStream(exportedFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (inChannel != null) {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } else {
                throw new IOException("inChannel size is null");
            }
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }

        return Single.just(exportedFile);
    }

    private File getExtStorageDir() {
        String APP_NAME = "Uttam";

        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), APP_NAME);
    }
}
