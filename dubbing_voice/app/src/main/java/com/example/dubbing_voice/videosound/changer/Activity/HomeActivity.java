package com.example.dubbing_voice.videosound.changer.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.AppConstants;


public class HomeActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 10;
    private static final int STORAGE_PERMISSION_CODE = 20;
    public static Activity home_activity;
    String BACK = "back";
    String CHANGE_AUDIO = "change";
    LinearLayout ChangeAudio;
    ImageView Img_back;
    String MUTE_AUDIO = "mute";
    LinearLayout MuteAudio;
    String RECORD_AUDIO = "record";
    LinearLayout RecordAudio;
    String action_name = "back";
    LinearLayout Crop;
    String CROP_VIDEO = "crop";
    LinearLayout slowandfastmotion;
    String SLOW_FAST_MOSTION = "Fast_motion";
    LinearLayout Video_filter;
    String VIDEO_FILTER = "Video_filter";
    LinearLayout Videotoaudio;
    String VIDEO_to_AUDIO = "Trim_Video";
    LinearLayout video_card_main;
    String Video_card_main = "video_card_main";


    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_home);
        home_activity = this;
        this.Img_back = findViewById(R.id.img_back);
        this.ChangeAudio = findViewById(R.id.change_audio_layout);
        this.MuteAudio = findViewById(R.id.mute_layout);
        this.RecordAudio = findViewById(R.id.record_layout);
        this.Crop = findViewById(R.id.crop_layout);
        this.Videotoaudio = findViewById(R.id.Videotoaudio);
        this.slowandfastmotion = findViewById(R.id.slowandfastmotion);
        this.video_card_main = findViewById(R.id.video_card_main);
        this.Video_filter = findViewById(R.id.Video_filter);
        this.Img_back.setOnClickListener(view -> HomeActivity.this.onBackPressed());
        this.ChangeAudio.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.CHANGE_AUDIO;

            HomeActivity.this.SelectVideoScreen();

        });
        this.MuteAudio.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.MUTE_AUDIO;

            HomeActivity.this.SelectVideoScreen();

        });
        this.RecordAudio.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.RECORD_AUDIO;

            HomeActivity.this.SelectVideoScreen();
        });
        this.Crop.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.CROP_VIDEO;
            HomeActivity.this.SelectVideoScreen();
        });
        this.slowandfastmotion.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.SLOW_FAST_MOSTION;
            HomeActivity.this.SelectVideoScreen();
        });
        this.Video_filter.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.VIDEO_FILTER;
            HomeActivity.this.SelectVideoScreen();
        });
        this.Videotoaudio.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = homeActivity.VIDEO_to_AUDIO;
            HomeActivity.this.SelectVideoScreen();
        });
        this.video_card_main.setOnClickListener(view -> {
            HomeActivity homeActivity = HomeActivity.this;
            homeActivity.action_name = String.valueOf(homeActivity.Video_card_main);
            HomeActivity.this.SelectVideoScreen();
        });
    }


    public void SelectVideoScreen() {
        Intent intent = new Intent(this, SelectVideoActivity.class);
        intent.putExtra("Actionanme", this.action_name);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.action_name = this.BACK;
        BackScreen();

    }

    private void BackScreen() {
        finish();
        AppConstants.overridePendingTransitionExit(this);
    }
}


