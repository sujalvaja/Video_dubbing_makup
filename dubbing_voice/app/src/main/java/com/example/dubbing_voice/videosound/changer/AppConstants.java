package com.example.dubbing_voice.videosound.changer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;



import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class AppConstants {


    public static void overridePendingTransitionExit(Activity activity) {
    }

    public static boolean isSdkHigherThan29() {
        return Build.VERSION.SDK_INT > 29;
    }

    public static void RefreshGallery(Context context, File file) {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
            return;
        }
        context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }


}
