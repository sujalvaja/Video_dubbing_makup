package com.example.dubbing_voice.videosound.changer.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.EUGeneralHelper;

import java.io.File;

public class MusicpreviewActivity extends AppCompatActivity {
    MediaPlayer music;
    LinearLayout share_layout;
    ImageView pause;
    ImageView img_back;
    LottieAnimationView imageview1;
    private String path;
    private String frompage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicpreview);
        this.path = getIntent().getStringExtra("videopath");
        this.frompage = getIntent().getStringExtra("frompage");
        share_layout = findViewById(R.id.share_layout);
        imageview1 = findViewById(R.id.imageview1);
        img_back = findViewById(R.id.img_back);
        pause = findViewById(R.id.pause);
        pause.setImageResource(R.drawable.play_icn);

        music = new MediaPlayer();
        music.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (music.isPlaying()) {
                    pause.setImageResource(R.drawable.play_icn);
                    music.pause();
                    imageview1.pauseAnimation();
                } else {
                    pause.setImageResource(R.drawable.pause_icn);
                    music.start();
                    imageview1.playAnimation();
                    imageview1.loop(true);
                }
            }

        });
        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share_Video();
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        music = MediaPlayer.create(
                this, Uri.parse(path));
    }

    public void Delete_Video(View view) {
        DeleteDialog();
    }

    public void Home_Video(View view) {
        if (this.frompage.equals("Folder")) {
            HomeScreen();
        } else {
            MyFolderScreen();
        }
    }

    private void MyFolderScreen() {
        CloseAllScreens();
        EUGeneralHelper.is_come_from_start = false;
        startActivity(new Intent(this, MyWorkActivity.class));
        finish();
    }

    private void HomeScreen() {
        CloseAllScreens();
        finish();
    }

    private void CloseAllScreens() {
        if (HomeActivity.home_activity != null) {
            HomeActivity.home_activity.finish();
        }
        if (ChangeMusicActivity.change_music_activity != null) {
            ChangeMusicActivity.change_music_activity.finish();
        }
        if (MusicListActivity.music_list_activity != null) {
            MusicListActivity.music_list_activity.finish();
        }
        if (MuteVideoActivity.mute_video_activity != null) {
            MuteVideoActivity.mute_video_activity.finish();
        }
        if (MyWorkActivity.my_work_activity != null) {
            MyWorkActivity.my_work_activity.finish();
        }
        if (RecordingActivity.recording_activity != null) {
            RecordingActivity.recording_activity.finish();
        }

    }

    public void Share_Video() {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/mp4");
            intent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + this.path));
            startActivity(Intent.createChooser(intent, "Compare"));
        } catch (Exception e) {
            e.toString();
        }
    }

    public boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] list = file.list();
            for (String file2 : list) {
                if (!deleteDir(new File(file, file2))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    @SuppressLint("SetTextI18n")
    private void DeleteDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Won't be delete this video!");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MusicpreviewActivity.this.deleteDir(new File(MusicpreviewActivity.this.path));
                MusicpreviewActivity.this.onBackPressed();
                dialog.dismiss();
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}