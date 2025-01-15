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

public class Slow_Motion extends AppCompatActivity {
    public static Activity slow_video_activity;
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
    SeekBar seekBar;
    TextView tvEndVideo;    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (Slow_Motion.this.videoview.isPlaying()) {
                int currentPosition = Slow_Motion.this.videoview.getCurrentPosition();
                Slow_Motion.this.seekBar.setProgress(currentPosition);
                try {
                    TextView textView = Slow_Motion.this.tvStartVideo;
                    textView.setText(Slow_Motion.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (currentPosition == Slow_Motion.this.duration) {
                    Slow_Motion.this.seekBar.setProgress(0);
                    Slow_Motion.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                    Slow_Motion.this.tvStartVideo.setText("00:00");
                    Slow_Motion.this.handler.removeCallbacks(Slow_Motion.this.seekrunnable);
                    return;
                }
                Slow_Motion.this.handler.postDelayed(Slow_Motion.this.seekrunnable, 500);
                return;
            }
            Slow_Motion.this.seekBar.setProgress(Slow_Motion.this.duration);
            try {
                TextView textView2 = Slow_Motion.this.tvStartVideo;
                textView2.setText(Slow_Motion.formatTimeUnit((long) Slow_Motion.this.duration));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
            Slow_Motion.this.handler.removeCallbacks(Slow_Motion.this.seekrunnable);
        }
    };
    TextView tvStartVideo;
    VideoView videoview;

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView((int) R.layout.activity_slow_motion);
            slow_video_activity = this;
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
                    if (!Slow_Motion.this.isPlay.booleanValue()) {
                        Slow_Motion.this.PlayVideo();
                    } else {
                        Slow_Motion.this.PauseVideo();
                    }
                }
            });
            this.img_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Slow_Motion.this.onBackPressed();
                }
            });
            this.button_MuteAudio.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean z = ContextCompat.checkSelfPermission(Slow_Motion.this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
                    Log.e("Permission:", "" + z);
                    Slow_Motion.this.SaveDialog();
                }
            });

            runOnUiThread(new Runnable() {
                public void run() {
                    Slow_Motion.this.PlayVideo();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void PauseVideo() {
        this.videoview.pause();
        this.handler.removeCallbacks(this.seekrunnable);
        this.btnPlay.setBackgroundResource(R.drawable.play_icn);
        this.isPlay = false;
    }


    public void PlayVideo() {
        this.duration = this.videoview.getDuration();
        this.videoview.seekTo(this.seekBar.getProgress());
        this.videoview.setVideoURI(Uri.parse(this.Videopath));
        this.videoview.start();
        this.btnPlay.setBackgroundResource(R.drawable.pause_icn);
        this.isPlay = true;
        this.videoview.setMediaController((MediaController) null);
        this.videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                try {
                    Slow_Motion Slow_Motion = Slow_Motion.this;
                    Slow_Motion.duration = Slow_Motion.videoview.getDuration();
                    Slow_Motion.this.seekBar.setMax(Slow_Motion.this.duration);
                    Slow_Motion.this.handler.postDelayed(Slow_Motion.this.seekrunnable, 200);
                    Slow_Motion.this.tvStartVideo.setText("00:00");
                    try {
                        TextView textView = Slow_Motion.this.tvEndVideo;
                        textView.setText(Slow_Motion.formatTimeUnit((long) Slow_Motion.this.duration));
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
                    Slow_Motion.this.videoview.seekTo(i);
                }
            }
        });
        this.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                Slow_Motion.this.videoview.seekTo(100);
                Slow_Motion.this.videoview.seekTo(0);
                Slow_Motion.this.seekBar.setProgress(0);
                Slow_Motion.this.tvStartVideo.setText("00:00");
                Slow_Motion.this.handler.removeCallbacks(Slow_Motion.this.seekrunnable);
            }
        });
    }

    public void MuteAudio() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name + "/slow_motion");
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        String absolutePath = new File(externalStoragePublicDirectory, ("Slow_motion_Video_" + String.valueOf(Calendar.getInstance().getTimeInMillis())) + ".mp4").getAbsolutePath();
        this.outputpath = absolutePath;
        String[] strArr = {"-y", "-i", Videopath, "-filter_complex", "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]", "-map", "[v]", "-map", "[a]", "-b:v", "2097k", "-r", "60", "-vcodec", "mpeg4", absolutePath};
        LoadingDialog();
        this.myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
        RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.myRxFFmpegSubscriber);
    }

    public void SaveDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Wont't able to save converted video?");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Slow_Motion.this.MuteAudio();
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
                if (Slow_Motion.this.myRxFFmpegSubscriber != null) {
                    Slow_Motion.this.myRxFFmpegSubscriber.dispose();
                }
                Slow_Motion.this.loading_dialog.dismiss();
            }
        });
    }

    public void CreateVideoScreen() {
        Intent intent = new Intent(this, VideoViewActivity.class);
        intent.putExtra("videopath", this.outputpath);
        intent.putExtra("frompage", "Mute");
        startActivity(intent);
        finish();
    }

    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        super.onBackPressed();
        BackScreen();

    }

    private void BackScreen() {
        finish();
    }

    public class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public MyRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(Slow_Motion.this.TAG, "SUCCESS with output : ");
            Slow_Motion.this.dialog_txt_loading_process.setText("Done");
            Slow_Motion.this.loading_dialog.dismiss();
            AppConstants.RefreshGallery(Slow_Motion.this, new File(Slow_Motion.this.outputpath));
            Slow_Motion Slow_Motion = Slow_Motion.this;
            Slow_Motion.action_name = Slow_Motion.MUTE_AUDIO;

            Slow_Motion.this.CreateVideoScreen();

        }

        public void onProgress(int i, long j) {
            TextView textView = Slow_Motion.this.dialog_txt_loading_process;
            String str = Slow_Motion.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(Slow_Motion.this.TAG, "process cancel : ");
            Slow_Motion.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(Slow_Motion.slow_video_activity, "process cancel");
        }

        public void onError(String str) {
            String str2 = Slow_Motion.this.TAG;
            Log.d(str2, "FAILED with output : " + str);
            Slow_Motion.this.loading_dialog.dismiss();
        }
    }


}
