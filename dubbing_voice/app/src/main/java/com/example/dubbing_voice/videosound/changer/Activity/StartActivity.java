package com.example.dubbing_voice.videosound.changer.Activity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.example.dubbing_voice.R;
import pub.devrel.easypermissions.EasyPermissions;


public class StartActivity extends Activity  {
    public static Activity start_activity;
    private static final int PERMISSION_REQUEST_CODE = 200;
    ImageView img_ad_free;
    RelativeLayout mywork_layout;
    RelativeLayout start_layout;
    ImageView img_info;

    
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_start);
        start_activity = this;

        checkPermission();
        this.start_layout = (RelativeLayout) findViewById(R.id.start_layout);
        this.img_info = (ImageView) findViewById(R.id.img_info);
        this.mywork_layout = (RelativeLayout) findViewById(R.id.mywork_layout);
        ImageView imageView = (ImageView) findViewById(R.id.img_ad_free);
        this.img_ad_free = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        img_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.start_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StartActivity.this.HomeScreen();
            }
        });
        this.mywork_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StartActivity.this.MyWorkScreen();
            }
        });

    }

    public void checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
            } else { //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }  else {
            //below android 11=======
            startActivity(new Intent(this, HomeActivity.class));
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        EasyPermissions.onRequestPermissionsResult(requestCode,
                permissions, grantResults, this);
    }
    public void HomeScreen() {
        startActivity(new Intent(this, HomeActivity.class));
    }
     public void MyWorkScreen() {
        startActivity(new Intent(this, MyWorkActivity.class));
    }

    public void onResume() {
        super.onResume();

    }
}
