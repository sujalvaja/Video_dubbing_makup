package com.example.dubbing_voice.videosound.changer.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.airbnb.lottie.LottieAnimationView;
import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.AppClass;
import com.example.dubbing_voice.videosound.changer.EUGeneralClass;

import java.io.File;
import java.util.Calendar;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class RecordingActivity extends AppCompatActivity {
    private static final String TAG = "ffmpeg";
    private static final int REQUEST_MICROPHONE = 100;
    public static Activity recording_activity;
    public Handler durationHandler = new Handler();
    public File record_outpath;
    String BACK = "back";
    String RECORD_AUDIO = "record";
    String Videoname;
    String Videopath;
    String action_name = "back";
    AudioRxFFmpegSubscriber audioRxFFmpegSubscriber;
    CountDownTimer f197cd;
    TextView dialog_txt_loading_process;
    ImageView ic_back;
    ImageView ic_done;
    ImageView img_record1;
    boolean is_record = false;
    Dialog loading_dialog;
    String outputpath;
    LottieAnimationView progressBarwaves;
    ImageView record_btn;
    TextView record_info;
    RelativeLayout rel_ad_layout;
    File sourceLocation;
    VideoView videoView;
    Runnable run = new Runnable() {
        public void run() {

            Toast.makeText(RecordingActivity.this, "stop", Toast.LENGTH_SHORT);
            Log.d("TAG20", "stop");

            if (RecordingActivity.this.videoView.isPlaying()) {
                RecordingActivity.this.durationHandler.postDelayed(this, 100);
            }
        }
    };
    private MediaRecorder recorder;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_record);
        recording_activity = this;
        this.videoView = (VideoView) findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        this.progressBarwaves = (LottieAnimationView) findViewById(R.id.progressBarwaves);
        this.ic_done = (ImageView) findViewById(R.id.ic_done);
        this.ic_back = (ImageView) findViewById(R.id.img_back);
        this.record_btn = (ImageView) findViewById(R.id.img_record);
        record_btn.setImageResource(R.drawable.play_icn);
        this.img_record1 = (ImageView) findViewById(R.id.img_record1);
        this.record_info = (TextView) findViewById(R.id.record_info);


        this.ic_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RecordingActivity.this.onBackPressed();
            }
        });
        this.ic_done.setVisibility(View.GONE);

        checkpermission(Manifest.permission.RECORD_AUDIO, 2);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(50);
        alphaAnimation.setStartOffset(20);
        alphaAnimation.setRepeatMode(2);
        alphaAnimation.setRepeatCount(-1);
        String string = getIntent().getExtras().getString("Filepath");
        this.Videopath = string;
        this.Videoname = string.substring(string.lastIndexOf(47) + 1, this.Videopath.length());
        File file = new File(this.Videopath);
        this.sourceLocation = file;
        this.videoView.setVideoURI(Uri.parse(file.getAbsolutePath()));
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0.0f, 0.0f);
            }
        });
        this.videoView.start();
        this.videoView.seekTo(100);
        this.record_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                {
                    if (recorder == null) {
                        RecordingActivity.this.recordClick();
                    } else {
                        RecordingActivity.this.stoprecoeding();

                    }
                }
            }
        });
        this.ic_done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RecordingActivity.this.SaveDialog();
            }
        });
        this.img_record1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RecordingActivity.this.stoprecoeding();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void recordClick() {
        try {
            if (this.videoView.isPlaying()) {
                this.videoView.pause();
            }
            this.videoView.seekTo(100);
            checkRecordDir();
            record_btn.setImageResource(R.drawable.pause_icn);
            MediaRecorder mediaRecorder = new MediaRecorder();
            this.recorder = mediaRecorder;
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            this.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            this.recorder.setMaxDuration(this.videoView.getDuration());
            this.recorder.setOutputFile(this.record_outpath.getAbsolutePath());
            this.recorder.prepare();
            this.recorder.start();
            this.videoView.start();

            this.record_info.setText("Please wait Until recording complete..");


            progressBarwaves.playAnimation();
            progressBarwaves.loop(true);
            Toast.makeText(getApplicationContext(), "Start Recording", Toast.LENGTH_SHORT).show();
            Log.d("TAG20", "Start Recording");
            this.durationHandler.postDelayed(this.run, 1000);
            this.f197cd = new CountDownTimer((long) (this.videoView.getDuration() + 1000), 1000) {
                int count = 0;

                public void onFinish() {
                    if (RecordingActivity.this.ic_done != null) {
                        RecordingActivity.this.ic_done.setVisibility(View.VISIBLE);
                    }
                    record_btn.setImageResource(R.drawable.play_icn);
                    RecordingActivity.this.is_record = true;
                    RecordingActivity.this.record_info.setText("Tap To Record");
                    RecordingActivity.this.tostop();
                    RecordingActivity.this.videoView.pause();
                    progressBarwaves.pauseAnimation();
                    Toast.makeText(getApplicationContext(), "stop recording", Toast.LENGTH_SHORT).show();
                    if (RecordingActivity.this.f197cd != null) {
                        RecordingActivity.this.f197cd.cancel();
                        RecordingActivity.this.f197cd = null;
                    }
                    RecordingActivity.this.SaveDialog();
                }

                public void onTick(long j) {
                    Log.e("Clocltick:", "" + this.count);
                    this.count = this.count + 1;
                }
            }.start();

        } catch (Exception unused) {
        }
    }

    private void stoprecoeding() {

        if (RecordingActivity.this.ic_done != null) {
            RecordingActivity.this.ic_done.setVisibility(View.GONE);
        }
        record_btn.setImageResource(R.drawable.play_icn);
        RecordingActivity.this.is_record = true;
        RecordingActivity.this.record_info.setText("Tap To Record");
        RecordingActivity.this.tostop();
        RecordingActivity.this.videoView.pause();
        progressBarwaves.pauseAnimation();
        Toast.makeText(getApplicationContext(), "stop recording", Toast.LENGTH_SHORT).show();
        if (RecordingActivity.this.f197cd != null) {
            RecordingActivity.this.f197cd.cancel();
            RecordingActivity.this.f197cd = null;
        }
        RecordingActivity.this.SaveDialog();


    }

    private void checkRecordDir() {
        File externalFilesDir = getExternalFilesDir(AppClass.folder_name);
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdirs();
        }
        File file = new File(externalFilesDir, ".RecordAudio");
        if (file.exists()) {
            deleteDir(file);
        }
        file.mkdir();
        this.record_outpath = new File(file, "recordaudio" + ".mp4");
    }

    public boolean deleteDir(File file) {
        String[] list;
        if (file.isDirectory() && (list = file.list()) != null) {
            for (String file2 : list) {
                if (!deleteDir(new File(file, file2))) {
                    return false;
                }
            }
        }
        return file.delete();
    }


    public void MergeRecordedAudio() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name + "/RecordingAudio");
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        File file = new File(externalStoragePublicDirectory, ("RecordMusic_" + Calendar.getInstance().getTimeInMillis()) + ".mp4");
        String absolutePath = this.sourceLocation.getAbsolutePath();
        String absolutePath2 = this.record_outpath.getAbsolutePath();
        String absolutePath3 = file.getAbsolutePath();
        this.outputpath = absolutePath3;
        String[] strArr = {TAG, "-i", absolutePath, "-i", absolutePath2, "-strict", "experimental", "-preset", "veryfast", "-map", "0:v", "-map", "1:a", "-s", "320x240", "-r", "30", "-b", "15496k", "-vcodec", "mpeg4", "-ab", "48000", "-ac", ExifInterface.GPS_MEASUREMENT_2D, "-ar", "22050", "-shortest", absolutePath3};
        LoadingDialog();
        this.audioRxFFmpegSubscriber = new AudioRxFFmpegSubscriber();
        RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.audioRxFFmpegSubscriber);
    }

    public void CreateVideoScreen() {
        Intent intent = new Intent(this, VideoViewActivity.class);
        intent.putExtra("videopath", this.outputpath);
        intent.putExtra("frompage", "Recording");
        startActivity(intent);
        finish();
    }

    public void tostop() {
        try {
            MediaRecorder mediaRecorder = this.recorder;
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                this.recorder.release();
                this.recorder = null;

            }
        } catch (RuntimeException unused) {
        }
    }

    public void InformDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Saved!!");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Your Recording completed!");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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

    public void SaveDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText(" You Wont't to save this video?");

        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RecordingActivity.this.MergeRecordedAudio();
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
                if (RecordingActivity.this.audioRxFFmpegSubscriber != null) {
                    RecordingActivity.this.audioRxFFmpegSubscriber.dispose();
                }
                RecordingActivity.this.loading_dialog.dismiss();
            }
        });
    }

    private void ExitDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Won't exit without save!");
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                RecordingActivity.this.BackScreen();
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void cleanUp() {
        MediaRecorder mediaRecorder = this.recorder;
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                this.recorder.release();
                this.recorder = null;
                throw th;
            }
            this.recorder.release();
            this.recorder = null;

        }
    }

    public void onResume() {
        super.onResume();

    }

    public void NextScreen() {
        if (this.action_name.equalsIgnoreCase(this.BACK)) {
            BackScreen();
        } else if (this.action_name.equalsIgnoreCase(this.RECORD_AUDIO)) {
            CreateVideoScreen();
        }
    }

    public void onBackPressed() {
        this.action_name = this.BACK;
        if (this.is_record) {
            ExitDialog();
        } else {
            BackScreen();
        }
    }

    public void checkpermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(RecordingActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(RecordingActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == REQUEST_MICROPHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RecordingActivity.this, "Microphone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RecordingActivity.this, "Microphone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void BackScreen() {
        cleanUp();
        CountDownTimer countDownTimer = this.f197cd;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.f197cd = null;
        }
        finish();
    }

    public class AudioRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public AudioRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(RecordingActivity.TAG, "SUCCESS with output : ");
            RecordingActivity.this.dialog_txt_loading_process.setText("Done");
            RecordingActivity.this.loading_dialog.dismiss();
            if (RecordingActivity.this.record_outpath.exists()) {
                RecordingActivity.this.record_outpath.delete();
            }
            RecordingActivity recordingActivity = RecordingActivity.this;
            recordingActivity.action_name = recordingActivity.RECORD_AUDIO;

            RecordingActivity.this.CreateVideoScreen();

        }

        public void onProgress(int i, long j) {
            TextView textView = RecordingActivity.this.dialog_txt_loading_process;
            textView.setText("Video Creation:: " + i + " %");
            String sb = "progress : " +
                    i;
            Log.d(RecordingActivity.TAG, sb);
        }

        public void onCancel() {
            Log.d(RecordingActivity.TAG, "process cancel : ");
            RecordingActivity.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(RecordingActivity.recording_activity, "process cancel");
        }

        public void onError(String str) {
            Log.d(RecordingActivity.TAG, "FAILED with output : " + str);
            RecordingActivity.this.loading_dialog.dismiss();
        }
    }
}

