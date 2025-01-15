package com.example.dubbing_voice.videosound.changer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Process;
import android.text.format.DateFormat;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.example.dubbing_voice.R;


import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import kotlin.UByte;

public class EUGeneralClass {
    



    private static Dialog loading_dialog;
    private static TextView loading_dialog_message;
    private static Context mContext;


    public EUGeneralClass(Context context) {
        mContext = context;
    }


    public static void ShowSuccessToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void ShowErrorToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void LoadingDialog(Context context, String str) {
        Dialog dialog = new Dialog(context, R.style.TransparentBackground);
        loading_dialog = dialog;
        dialog.requestWindowFeature(1);
        loading_dialog.setContentView(R.layout.dialog_loading);
        loading_dialog_message = (TextView) loading_dialog.findViewById(R.id.dialog_loading_txt_message);
        loading_dialog_message.setTypeface(Typeface.createFromAsset(context.getAssets(), EUGeneralHelper.roboto_font_path));
        loading_dialog_message.setText(str);
        loading_dialog.show();
    }
}
