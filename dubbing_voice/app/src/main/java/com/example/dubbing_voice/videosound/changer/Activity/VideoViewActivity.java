package com.example.dubbing_voice.videosound.changer.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.EUGeneralHelper;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoViewActivity extends AppCompatActivity {
    public static Activity video_view_activity;
    ImageView Home;

    ImageView btnPlay;
    int duration;
    String frompage;
    Handler handler = new Handler();
    LinearLayout home_layout;
    ImageView ic_back;
    Boolean isPlay = false;
    String path;
    RelativeLayout rel_ad_layout;
    SeekBar seekBar;
    TextView tvEndVideo;    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (VideoViewActivity.this.videoView.isPlaying()) {
                int currentPosition = VideoViewActivity.this.videoView.getCurrentPosition();
                VideoViewActivity.this.seekBar.setProgress(currentPosition);
                try {
                    TextView textView = VideoViewActivity.this.tvStartVideo;
                    textView.setText(VideoViewActivity.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (currentPosition == VideoViewActivity.this.duration) {
                    VideoViewActivity.this.seekBar.setProgress(0);
                    VideoViewActivity.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                    VideoViewActivity.this.tvStartVideo.setText("00:00");
                    VideoViewActivity.this.handler.removeCallbacks(VideoViewActivity.this.seekrunnable);
                    return;
                }
                VideoViewActivity.this.handler.postDelayed(VideoViewActivity.this.seekrunnable, 500);
                return;
            }
            VideoViewActivity.this.seekBar.setProgress(VideoViewActivity.this.duration);
            try {
                TextView textView2 = VideoViewActivity.this.tvStartVideo;
                textView2.setText(VideoViewActivity.formatTimeUnit((long) VideoViewActivity.this.duration));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
            VideoViewActivity.this.handler.removeCallbacks(VideoViewActivity.this.seekrunnable);
        }
    };
    TextView tvStartVideo;
    TextView txt_home;
    VideoView videoView;

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            requestWindowFeature(1);
            getWindow().setFlags(1024, 1024);
            setContentView((int) R.layout.activity_video_view);
            video_view_activity = this;
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
            this.path = getIntent().getStringExtra("videopath");
            this.frompage = getIntent().getStringExtra("frompage");
            this.videoView = (VideoView) findViewById(R.id.videoView);
            this.seekBar = (SeekBar) findViewById(R.id.sbVideo);
            this.tvStartVideo = (TextView) findViewById(R.id.tvStartVideo);
            this.tvEndVideo = (TextView) findViewById(R.id.tvEndVideo);
            this.btnPlay = (ImageView) findViewById(R.id.btnPlayVideo);
            this.Home = (ImageView) findViewById(R.id.btn_home);
            this.home_layout = (LinearLayout) findViewById(R.id.home_layout);
            this.txt_home = (TextView) findViewById(R.id.txt_home);
            this.ic_back = (ImageView) findViewById(R.id.img_back);
            if (this.frompage.equals("Folder")) {
                this.Home.setImageResource(R.drawable.history_btn);
                this.txt_home.setText("Home");
            } else {
                //  this.Home.setImageResource(R.drawable.plus);
                this.txt_home.setText("My Work");
            }
            this.btnPlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!VideoViewActivity.this.isPlay.booleanValue()) {
                        VideoViewActivity.this.PlayVideo();
                    } else {
                        VideoViewActivity.this.PauseVideo();
                    }
                }
            });
            this.ic_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    VideoViewActivity.this.onBackPressed();
                }
            });
            Log.e("Saved File Path:", this.path);
            PlayVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void PauseVideo() {
        this.videoView.pause();
        this.handler.removeCallbacks(this.seekrunnable);
        this.btnPlay.setBackgroundResource(R.drawable.play_icn);
        this.isPlay = false;
    }


    public void PlayVideo() {
        this.duration = this.videoView.getDuration();
        this.videoView.setVideoPath(this.path);
        this.videoView.seekTo(0);
        this.videoView.start();
        this.btnPlay.setBackgroundResource(R.drawable.pause_icn);
        this.isPlay = true;
        this.videoView.setMediaController((MediaController) null);
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    VideoViewActivity videoViewActivity = VideoViewActivity.this;
                    videoViewActivity.duration = videoViewActivity.videoView.getDuration();
                    VideoViewActivity.this.seekBar.setMax(VideoViewActivity.this.duration);
                    VideoViewActivity.this.handler.postDelayed(VideoViewActivity.this.seekrunnable, 200);
                    VideoViewActivity.this.tvStartVideo.setText("00:00");
                    try {
                        TextView textView = VideoViewActivity.this.tvEndVideo;
                        textView.setText(VideoViewActivity.formatTimeUnit((long) VideoViewActivity.this.duration));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    VideoViewActivity.this.videoView.seekTo(i);
                }
            }
        });
        this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                VideoViewActivity.this.videoView.seekTo(100);
                VideoViewActivity.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                VideoViewActivity.this.videoView.seekTo(0);
                VideoViewActivity.this.seekBar.setProgress(0);
                VideoViewActivity.this.tvStartVideo.setText("00:00");
                VideoViewActivity.this.handler.removeCallbacks(VideoViewActivity.this.seekrunnable);
            }
        });
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

    public void Share_Video(View view) {
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

    private void DeleteDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Won't be delete this video!");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoViewActivity.this.deleteDir(new File(VideoViewActivity.this.path));
                VideoViewActivity.this.onBackPressed();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onResume() {
        super.onResume();

    }

    public void BackScreen() {
        finish();
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


}