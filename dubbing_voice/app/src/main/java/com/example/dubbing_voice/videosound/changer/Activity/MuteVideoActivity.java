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
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.AppClass;
import com.example.dubbing_voice.videosound.changer.AppConstants;
import com.example.dubbing_voice.videosound.changer.EUGeneralClass;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class MuteVideoActivity extends AppCompatActivity {
    public static Activity mute_video_activity;
    String BACK = "back";
    String MUTE_AUDIO = "mute";
    String TAG = "video_convertor";
    String Videoname;
    String Videopath;
    String action_name = "back";
   
    ImageView btnPlay;
    RelativeLayout button_MuteAudio;
    TextView dialog_txt_loading_process;
    int duration;
    Handler handler = new Handler();
    ImageView img_back;
   
    Boolean isPlay = false;
    Dialog loading_dialog;
    MyRxFFmpegSubscriber myRxFFmpegSubscriber;
    String outputpath = "";
    RelativeLayout rel_ad_layout;
    SeekBar seekBar;
    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (MuteVideoActivity.this.videoview.isPlaying()) {
                int currentPosition = MuteVideoActivity.this.videoview.getCurrentPosition();
                MuteVideoActivity.this.seekBar.setProgress(currentPosition);
                try {
                    TextView textView = MuteVideoActivity.this.tvStartVideo;
                    textView.setText(MuteVideoActivity.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (currentPosition == MuteVideoActivity.this.duration) {
                    MuteVideoActivity.this.seekBar.setProgress(0);
                    MuteVideoActivity.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                    MuteVideoActivity.this.tvStartVideo.setText("00:00");
                    MuteVideoActivity.this.handler.removeCallbacks(MuteVideoActivity.this.seekrunnable);
                    return;
                }
                MuteVideoActivity.this.handler.postDelayed(MuteVideoActivity.this.seekrunnable, 500);
                return;
            }
            MuteVideoActivity.this.seekBar.setProgress(MuteVideoActivity.this.duration);
            try {
                TextView textView2 = MuteVideoActivity.this.tvStartVideo;
                textView2.setText(MuteVideoActivity.formatTimeUnit((long) MuteVideoActivity.this.duration));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
            MuteVideoActivity.this.handler.removeCallbacks(MuteVideoActivity.this.seekrunnable);
        }
    };
    TextView tvEndVideo;
    TextView tvStartVideo;
    VideoView videoview;



    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView((int) R.layout.activity_mute_video);
            mute_video_activity = this;
            String stringExtra = getIntent().getStringExtra("Filepath");
            this.Videopath = stringExtra;
            this.Videoname = stringExtra.substring(stringExtra.lastIndexOf(47) + 1, this.Videopath.length());
            this.videoview = (VideoView) findViewById(R.id.videoView);
            this.seekBar = (SeekBar) findViewById(R.id.sbVideo);
            this.tvStartVideo = (TextView) findViewById(R.id.tvStartVideo);
            this.tvEndVideo = (TextView) findViewById(R.id.tvEndVideo);
            this.btnPlay = (ImageView) findViewById(R.id.btnPlayVideo);

            this.button_MuteAudio = (RelativeLayout) findViewById(R.id.btn_layout);
            this.img_back = (ImageView) findViewById(R.id.img_back);
            this.btnPlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!MuteVideoActivity.this.isPlay.booleanValue()) {
                        MuteVideoActivity.this.PlayVideo();
                    } else {
                        MuteVideoActivity.this.PauseVideo();
                    }
                }
            });
            this.img_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MuteVideoActivity.this.onBackPressed();
                }
            });
            this.button_MuteAudio.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean z = ContextCompat.checkSelfPermission(MuteVideoActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
                    Log.e("Permission:", "" + z);
                    MuteVideoActivity.this.SaveDialog();
                }
            });

            runOnUiThread(new Runnable() {
                public void run() {
                    MuteVideoActivity.this.PlayVideo();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    public void PauseVideo() {
        this.videoview.pause();
        this.handler.removeCallbacks(this.seekrunnable);
        this.btnPlay.setBackgroundResource(R.drawable.audio_on);
        this.isPlay = false;
    }

   
    public void PlayVideo() {
        this.duration = this.videoview.getDuration();
        this.videoview.seekTo(this.seekBar.getProgress());
        this.videoview.setVideoURI(Uri.parse(this.Videopath));
        this.videoview.start();
        this.btnPlay.setBackgroundResource(R.drawable.audio_mute);
        this.isPlay = true;
        this.videoview.setMediaController((MediaController) null);
        this.videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    MuteVideoActivity muteVideoActivity = MuteVideoActivity.this;
                    muteVideoActivity.duration = muteVideoActivity.videoview.getDuration();
                    MuteVideoActivity.this.seekBar.setMax(MuteVideoActivity.this.duration);
                    MuteVideoActivity.this.handler.postDelayed(MuteVideoActivity.this.seekrunnable, 200);
                    MuteVideoActivity.this.tvStartVideo.setText("00:00");
                    try {
                        TextView textView = MuteVideoActivity.this.tvEndVideo;
                        textView.setText(MuteVideoActivity.formatTimeUnit((long) MuteVideoActivity.this.duration));
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
                    MuteVideoActivity.this.videoview.seekTo(i);
                }
            }
        });
        this.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                MuteVideoActivity.this.videoview.seekTo(100);
                MuteVideoActivity.this.btnPlay.setBackgroundResource(R.drawable.audio_on);
                MuteVideoActivity.this.videoview.seekTo(0);
                MuteVideoActivity.this.seekBar.setProgress(0);
                MuteVideoActivity.this.tvStartVideo.setText("00:00");
                MuteVideoActivity.this.handler.removeCallbacks(MuteVideoActivity.this.seekrunnable);
            }
        });
    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

   
    public void MuteAudio() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name + "/MuteVideo ");
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        String absolutePath = new File(externalStoragePublicDirectory, ("MuteVideo_" + String.valueOf(Calendar.getInstance().getTimeInMillis())) + ".mp4").getAbsolutePath();
        this.outputpath = absolutePath;
        String[] strArr = {"ffmpeg", "-i", this.Videopath, "-an", "-b:v", "2097k", "-r", "60", "-vcodec", "copy", "-preset", "ultrafast", absolutePath};
        LoadingDialog();
        this.myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
        RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.myRxFFmpegSubscriber);
    }

    public class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public MyRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(MuteVideoActivity.this.TAG, "SUCCESS with output : ");
            MuteVideoActivity.this.dialog_txt_loading_process.setText("Done");
            MuteVideoActivity.this.loading_dialog.dismiss();
            AppConstants.RefreshGallery(MuteVideoActivity.this, new File(MuteVideoActivity.this.outputpath));
            MuteVideoActivity muteVideoActivity = MuteVideoActivity.this;
            muteVideoActivity.action_name = muteVideoActivity.MUTE_AUDIO;
            CreateVideoScreen();
        }

        public void onProgress(int i, long j) {
            TextView textView = MuteVideoActivity.this.dialog_txt_loading_process;
            textView.setText("Video Creation:: " + i + " %");
            String str = MuteVideoActivity.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(MuteVideoActivity.this.TAG, "process cancel : ");
            MuteVideoActivity.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(MuteVideoActivity.mute_video_activity, "process cancel");
        }

        public void onError(String str) {
            String str2 = MuteVideoActivity.this.TAG;
            Log.d(str2, "FAILED with output : " + str);
            MuteVideoActivity.this.loading_dialog.dismiss();
        }
    }

   
    public void SaveDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("You Wont't to save this video?");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MuteVideoActivity.this.MuteAudio();
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

    private void LoadingDialog() {
        Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        this.loading_dialog = dialog;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.loading_dialog.setContentView(R.layout.dialog_video_create_loader);
        this.dialog_txt_loading_process = (TextView) this.loading_dialog.findViewById(R.id.dialog_loading_process);
        this.loading_dialog.show();
        ((RelativeLayout) this.loading_dialog.findViewById(R.id.dialog_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MuteVideoActivity.this.myRxFFmpegSubscriber != null) {
                    MuteVideoActivity.this.myRxFFmpegSubscriber.dispose();
                }
                MuteVideoActivity.this.loading_dialog.dismiss();
            }
        });
    }


    public void CreateVideoScreen() {
        Intent intent = new Intent(this, VideoViewActivity.class);
        intent.putExtra("videopath", this.outputpath);
        Log.d("TAG12","mutevideo file path : " +outputpath);
        intent.putExtra("frompage", "Mute");
        startActivity(intent);
        finish();
    }


    public void onResume() {
        super.onResume();
      
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.action_name = this.BACK;
   
            BackScreen();
        
    }

    private void BackScreen() {
        finish();
    }
}

