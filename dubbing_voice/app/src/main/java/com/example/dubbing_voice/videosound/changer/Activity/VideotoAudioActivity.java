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

public class VideotoAudioActivity extends AppCompatActivity {
    public static final int READ_WRITE_STORAGE = 52;
    public static Activity video_to_Audio_Activity;
    String BACK = "back";
    String TAG = "video_convertor";
    String Videoname;
    String Videopath;
    String action_name = "back";
    String MUTE_AUDIO = "mute";
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
            if (VideotoAudioActivity.this.videoview.isPlaying()) {
                int currentPosition = VideotoAudioActivity.this.videoview.getCurrentPosition();
                VideotoAudioActivity.this.seekBar.setProgress(currentPosition);
                try {
                    TextView textView = VideotoAudioActivity.this.tvStartVideo;
                    textView.setText(VideotoAudioActivity.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (currentPosition == VideotoAudioActivity.this.duration) {
                    VideotoAudioActivity.this.seekBar.setProgress(0);
                     VideotoAudioActivity.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                    VideotoAudioActivity.this.tvStartVideo.setText("00:00");
                    VideotoAudioActivity.this.handler.removeCallbacks(VideotoAudioActivity.this.seekrunnable);
                    return;
                }
                VideotoAudioActivity.this.handler.postDelayed(VideotoAudioActivity.this.seekrunnable, 500);
                return;
            }
            VideotoAudioActivity.this.seekBar.setProgress(VideotoAudioActivity.this.duration);
            try {
                TextView textView2 = VideotoAudioActivity.this.tvStartVideo;
                textView2.setText(VideotoAudioActivity.formatTimeUnit((long) VideotoAudioActivity.this.duration));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
            VideotoAudioActivity.this.handler.removeCallbacks(VideotoAudioActivity.this.seekrunnable);
        }
    };
    TextView tvEndVideo;
    TextView tvStartVideo;
    VideoView videoview;


    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView((int) R.layout.activity_videoto_audio);
            video_to_Audio_Activity = this;
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
                    if (!VideotoAudioActivity.this.isPlay.booleanValue()) {
                        VideotoAudioActivity.this.PlayVideo();
                    } else {
                        VideotoAudioActivity.this.PauseVideo();
                    }
                }
            });
            this.img_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    VideotoAudioActivity.this.onBackPressed();
                }
            });
            this.button_MuteAudio.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean z = ContextCompat.checkSelfPermission(VideotoAudioActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
                    Log.e("Permission:", "" + z);
                    VideotoAudioActivity.this.SaveDialog();
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    VideotoAudioActivity.this.PlayVideo();
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
                    VideotoAudioActivity VideotoAudioActivity = VideotoAudioActivity.this;
                    VideotoAudioActivity.duration = VideotoAudioActivity.videoview.getDuration();
                    VideotoAudioActivity.this.seekBar.setMax(VideotoAudioActivity.this.duration);
                    VideotoAudioActivity.this.handler.postDelayed(VideotoAudioActivity.this.seekrunnable, 200);
                    VideotoAudioActivity.this.tvStartVideo.setText("00:00");
                    try {
                        TextView textView = VideotoAudioActivity.this.tvEndVideo;
                        textView.setText(VideotoAudioActivity.formatTimeUnit((long) VideotoAudioActivity.this.duration));
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
                    VideotoAudioActivity.this.videoview.seekTo(i);
                }
            }
        });
        this.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                VideotoAudioActivity.this.videoview.seekTo(100);
                VideotoAudioActivity.this.videoview.seekTo(0);
                VideotoAudioActivity.this.seekBar.setProgress(0);
                VideotoAudioActivity.this.tvStartVideo.setText("00:00");
                VideotoAudioActivity.this.handler.removeCallbacks(VideotoAudioActivity.this.seekrunnable);
            }
        });
    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }


    public void MuteAudio() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name+"/Video_to_audio_converter");
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        String absolutePath1 = new File(externalStoragePublicDirectory, ("Video_to_audio_converter_" + String.valueOf(Calendar.getInstance().getTimeInMillis())) + ".mp3").getAbsolutePath();
        this.outputpath = absolutePath1;
        String[]  strArr= {"-y", "-i", this.Videopath, "-vn", "-ar", "44100", "-ac", "2", "-b:a", "256k", "-f", "mp3", absolutePath1};
        LoadingDialog();
        this.myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
        RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.myRxFFmpegSubscriber);
    }

    public class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public MyRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(VideotoAudioActivity.this.TAG, "SUCCESS with output : ");
            VideotoAudioActivity.this.dialog_txt_loading_process.setText("Done");
            VideotoAudioActivity.this.loading_dialog.dismiss();
            AppConstants.RefreshGallery(VideotoAudioActivity.this, new File(VideotoAudioActivity.this.outputpath));
            VideotoAudioActivity VideotoAudioActivity = VideotoAudioActivity.this;
            VideotoAudioActivity.action_name = VideotoAudioActivity.MUTE_AUDIO;

            VideotoAudioActivity.this.CreateVideoScreen();

        }

        public void onProgress(int i, long j) {
            TextView textView = VideotoAudioActivity.this.dialog_txt_loading_process;
            textView.setText("Video Creation:: " + i + " %");
            String str = VideotoAudioActivity.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(VideotoAudioActivity.this.TAG, "process cancel : ");
            VideotoAudioActivity.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(VideotoAudioActivity.video_to_Audio_Activity, "process cancel");
        }

        public void onError(String str) {
            String str2 = VideotoAudioActivity.this.TAG;
            Log.d(str2, "FAILED with output : " + str);
            VideotoAudioActivity.this.loading_dialog.dismiss();
        }
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
                VideotoAudioActivity.this.MuteAudio();
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
                if (VideotoAudioActivity.this.myRxFFmpegSubscriber != null) {
                    VideotoAudioActivity.this.myRxFFmpegSubscriber.dispose();
                }
                VideotoAudioActivity.this.loading_dialog.dismiss();
            }
        });
    }
    public void CreateVideoScreen() {
        Intent intent = new Intent(this, MusicpreviewActivity.class);
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
}
