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

public class Fast_forward extends AppCompatActivity {
    public static final int READ_WRITE_STORAGE = 52;
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
    TextView tvEndVideo;    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (Fast_forward.this.videoview.isPlaying()) {
                int currentPosition = Fast_forward.this.videoview.getCurrentPosition();
                Fast_forward.this.seekBar.setProgress(currentPosition);
                try {
                    TextView textView = Fast_forward.this.tvStartVideo;
                    textView.setText(Fast_forward.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (currentPosition == Fast_forward.this.duration) {
                    Fast_forward.this.seekBar.setProgress(0);
                    Fast_forward.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                    Fast_forward.this.tvStartVideo.setText("00:00");
                    Fast_forward.this.handler.removeCallbacks(Fast_forward.this.seekrunnable);
                    return;
                }
                Fast_forward.this.handler.postDelayed(Fast_forward.this.seekrunnable, 500);
                return;
            }
            Fast_forward.this.seekBar.setProgress(Fast_forward.this.duration);
            try {
                TextView textView2 = Fast_forward.this.tvStartVideo;
                textView2.setText(Fast_forward.formatTimeUnit((long) Fast_forward.this.duration));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
            Fast_forward.this.handler.removeCallbacks(Fast_forward.this.seekrunnable);
        }
    };
    TextView tvStartVideo;
    VideoView videoview;
    ImageView ic_done;

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView((int) R.layout.activity_fast_forward);
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
                    if (!Fast_forward.this.isPlay.booleanValue()) {
                        Fast_forward.this.PlayVideo();
                    } else {
                        Fast_forward.this.PauseVideo();
                    }
                }
            });
            this.img_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Fast_forward.this.onBackPressed();
                }
            });
            this.button_MuteAudio.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean z = ContextCompat.checkSelfPermission(Fast_forward.this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
                    Log.e("Permission:", "" + z);
                    Fast_forward.this.SaveDialog();
                }
            });

            runOnUiThread(new Runnable() {
                public void run() {
                    Fast_forward.this.PlayVideo();
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
                    Fast_forward Fast_forward = Fast_forward.this;
                    Fast_forward.duration = Fast_forward.videoview.getDuration();
                    Fast_forward.this.seekBar.setMax(Fast_forward.this.duration);
                    Fast_forward.this.handler.postDelayed(Fast_forward.this.seekrunnable, 200);
                    Fast_forward.this.tvStartVideo.setText("00:00");
                    try {
                        TextView textView = Fast_forward.this.tvEndVideo;
                        textView.setText(Fast_forward.formatTimeUnit((long) Fast_forward.this.duration));
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
                    Fast_forward.this.videoview.seekTo(i);
                }
            }
        });
        this.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                Fast_forward.this.videoview.seekTo(100);
                Fast_forward.this.videoview.seekTo(0);
                Fast_forward.this.seekBar.setProgress(0);
                Fast_forward.this.tvStartVideo.setText("00:00");
                Fast_forward.this.handler.removeCallbacks(Fast_forward.this.seekrunnable);
            }
        });
    }

    public void MuteAudio() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name + "/Fast_Forward");
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        String absolutePath = new File(externalStoragePublicDirectory, ("FAst_forward_" + String.valueOf(Calendar.getInstance().getTimeInMillis())) + ".mp4").getAbsolutePath();
        this.outputpath = absolutePath;
        String[] strArr = {"ffmpeg", "-i", this.Videopath, "-filter_complex", "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2[a]", "-map", "[v]", "-map", "[a]", absolutePath};
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
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("You Wont't to save this video?");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fast_forward.this.MuteAudio();
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
                if (Fast_forward.this.myRxFFmpegSubscriber != null) {
                    Fast_forward.this.myRxFFmpegSubscriber.dispose();
                }
                Fast_forward.this.loading_dialog.dismiss();
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
            Log.e(Fast_forward.this.TAG, "SUCCESS with output : ");
            Fast_forward.this.dialog_txt_loading_process.setText("Done");
            Fast_forward.this.loading_dialog.dismiss();
            AppConstants.RefreshGallery(Fast_forward.this, new File(Fast_forward.this.outputpath));
            Fast_forward Fast_forward = Fast_forward.this;
            Fast_forward.action_name = Fast_forward.MUTE_AUDIO;

            Fast_forward.this.CreateVideoScreen();

        }

        public void onProgress(int i, long j) {
            String str = Fast_forward.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(Fast_forward.this.TAG, "process cancel : ");
            Fast_forward.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(Fast_forward.mute_video_activity, "process cancel");
        }

        public void onError(String str) {
            String str2 = Fast_forward.this.TAG;
            Log.d(str2, "FAILED with output : " + str);
            Fast_forward.this.loading_dialog.dismiss();
        }
    }


}
