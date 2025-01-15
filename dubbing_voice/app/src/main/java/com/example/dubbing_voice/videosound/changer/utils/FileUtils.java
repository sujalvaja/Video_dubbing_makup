package com.example.dubbing_voice.videosound.changer.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtils {



    public static final int FLAG_SUCCESS = 1;
    public static final int FLAG_EXISTS = 2;
    public static final int FLAG_FAILED = 3;



    public static int CreateFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Log.e("TAG13", "The file [ " + filePath + " ] has already exists");
            return FLAG_EXISTS;
        }
        if (filePath.endsWith(File.separator)) {
            Log.e("TAG13", "The file [ " + filePath + " ] can not be a directory");
            return FLAG_FAILED;
        }

        if (!file.getParentFile().exists()) {
            Log.d("TAG13", "creating parent directory...");
            if (!file.getParentFile().mkdirs()) {
                Log.e("TAG13", "created parent directory failed.");
                return FLAG_FAILED;
            }
        }


        try {
            if (file.createNewFile()) {
                Log.i("TAG13", "create file [ " + filePath + " ] success");
                return FLAG_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TAG13", "create file [ " + filePath + " ] failed");
            return FLAG_FAILED;
        }

        return FLAG_FAILED;
    }

    public static int createDir(String dirPath) {

        File dir = new File(dirPath);

        if (dir.exists()) {
            Log.w("TAG13", "The directory [ " + dirPath + " ] has already exists");
            return FLAG_EXISTS;
        }
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }

        if (dir.mkdirs()) {
            Log.d("TAG13", "create directory [ " + dirPath + " ] success");
            return FLAG_SUCCESS;
        }

        Log.e("TAG13", "create directory [ " + dirPath + " ] failed");
        return FLAG_FAILED;
    }
}

