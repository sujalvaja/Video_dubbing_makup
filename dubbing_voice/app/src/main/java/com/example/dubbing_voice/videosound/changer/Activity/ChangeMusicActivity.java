package com.example.dubbing_voice.videosound.changer.Activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.AppClass;
import com.example.dubbing_voice.videosound.changer.EUGeneralClass;
import com.sinaseyfi.advancedcardview.AdvancedCardView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class ChangeMusicActivity extends AppCompatActivity {
    public static Activity change_music_activity;
    String BACK = "back";
    String FinalAudioPath;
    String Next = "next";
    Uri SelectedAudioUri = null;
    String TAG = "ChangeAudio";
    String TrimAudioPath;
    String Videopath;
    String action_name = "back";

    TextView adtxtend;
    TextView adtxtstart;
    String aud_outputpath;
    File audioIp;
    AudioRxFFmpegSubscriber audioRxFFmpegSubscriber;
    ImageView audio_cancle;
    int audio_duration;
    RelativeLayout audio_info_layout;
    TextView audio_name;

    ImageView btnPlay;
    ImageView button_musicchange;
    TextView dialog_txt_loading_process;
    int duration;
    Handler handler = new Handler();
    ImageView ic_done;
    ImageView img_back;

    Boolean isPlay = false;
    boolean is_audio = false;
    JoinAudioRxFFmpegSubscriber joinaudioRxFFmpegSubscriber;
    Dialog loading_dialog;
    String lvalue;
    MediaPlayer mMediaPlayer;
    Handler mphandler = new Handler();
    String outputpath = "";
    SeekBar rangepointseekbar;
    AdvancedCardView rangseek_layout;
    RelativeLayout rel_ad_layout;
    Runnable runnable;
    SeekBar seekBar;
    String svalue;    Runnable seekrunnable = new Runnable() {
        public void run() {
            try {
                if (ChangeMusicActivity.this.videoview.isPlaying()) {
                    int currentPosition = ChangeMusicActivity.this.videoview.getCurrentPosition();
                    ChangeMusicActivity.this.seekBar.setProgress(currentPosition);
                    TextView textView = ChangeMusicActivity.this.tvStartVideo;
                    textView.setText(ChangeMusicActivity.formatTimeUnit((long) currentPosition));
                    if (currentPosition == ChangeMusicActivity.this.duration) {
                        ChangeMusicActivity.this.seekBar.setProgress(0);
                        ChangeMusicActivity.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                        ChangeMusicActivity.this.tvStartVideo.setText("00:00");
                        ChangeMusicActivity.this.handler.removeCallbacks(ChangeMusicActivity.this.seekrunnable);
                        return;
                    }
                    ChangeMusicActivity.this.handler.postDelayed(ChangeMusicActivity.this.seekrunnable, 500);
                    return;
                }
                ChangeMusicActivity.this.handler.removeCallbacks(ChangeMusicActivity.this.seekrunnable);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };
    TextView tvEndVideo;
    TextView tvStartVideo;
    int updated_audio;
    VideoRxFFmpegSubscriber videoRxFFmpegSubscriber;
    int video_duration;
    VideoView videoview;

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView((int) R.layout.activity_change_music);
            change_music_activity = this;
            this.Videopath = getIntent().getStringExtra("Filepath");
            ImageView button = (ImageView) findViewById(R.id.button_musicchange);
            this.button_musicchange = button;
            button.setTag("Change Music");
            this.videoview = (VideoView) findViewById(R.id.videoView);
            this.seekBar = (SeekBar) findViewById(R.id.sbVideo);
            this.tvStartVideo = (TextView) findViewById(R.id.tvStartVideo);
            this.tvEndVideo = (TextView) findViewById(R.id.tvEndVideo);
            this.rangepointseekbar = (SeekBar) findViewById(R.id.audio_seek);
            this.adtxtstart = (TextView) findViewById(R.id.adtxtstart);
            this.adtxtend = (TextView) findViewById(R.id.adtxtend);
            this.audio_name = (TextView) findViewById(R.id.audio_name);
            this.audio_cancle = (ImageView) findViewById(R.id.audio_cancle);
            this.ic_done = (ImageView) findViewById(R.id.ic_done);
            this.img_back = (ImageView) findViewById(R.id.img_back);
            AdvancedCardView advancedCardView = (AdvancedCardView) findViewById(R.id.rangseek_layout);
            this.rangseek_layout = advancedCardView;
            if (advancedCardView.getVisibility() == View.VISIBLE) {
                this.rangseek_layout.setVisibility(View.GONE);
            }
            this.audio_info_layout = (RelativeLayout) findViewById(R.id.audio_info_layout);
            ImageView imageView = (ImageView) findViewById(R.id.btnPlayVideo);
            this.btnPlay = imageView;
            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!ChangeMusicActivity.this.isPlay.booleanValue()) {
                        ChangeMusicActivity.this.PlayVideo();
                    } else {
                        ChangeMusicActivity.this.PauseVideo();
                    }
                }
            });
            this.audio_cancle.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ChangeMusicActivity.this.is_audio = false;
                    ChangeMusicActivity.this.audio_info_layout.setVisibility(View.GONE);
                    ChangeMusicActivity.this.PauseVideo();
                }
            });
            this.ic_done.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ChangeMusicActivity.this.Create();
                }
            });
            this.img_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ChangeMusicActivity.this.onBackPressed();
                }
            });
            runOnUiThread(new Runnable() {
                public void run() {
                    ChangeMusicActivity.this.PlayVideo();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void PauseVideo() {
        VideoView videoView = this.videoview;
        if (videoView != null) {
            videoView.pause();
            this.handler.removeCallbacks(this.seekrunnable);
            this.btnPlay.setBackgroundResource(R.drawable.play_icn);
            this.isPlay = false;
            PauseAudio();
        }
    }

    public void PlayVideo() {
        if (this.Videopath != null) {
            this.duration = this.videoview.getDuration();
            Log.e("Video Path:", String.valueOf(this.Videopath.trim()));
            this.videoview.setVideoURI(Uri.parse(this.Videopath));
            this.videoview.seekTo(0);
            this.videoview.start();
            this.btnPlay.setBackgroundResource(R.drawable.pause_icn);
            this.isPlay = true;
            this.videoview.setMediaController((MediaController) null);
            this.videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    try {
                        ChangeMusicActivity changeMusicActivity = ChangeMusicActivity.this;
                        changeMusicActivity.duration = changeMusicActivity.videoview.getDuration();
                        ChangeMusicActivity.this.video_duration = mediaPlayer.getDuration() / 1000;
                        ChangeMusicActivity.this.seekBar.setMax(ChangeMusicActivity.this.duration);
                        ChangeMusicActivity.this.handler.postDelayed(ChangeMusicActivity.this.seekrunnable, 200);
                        ChangeMusicActivity.this.tvStartVideo.setText("00:00");
                        TextView textView = ChangeMusicActivity.this.tvEndVideo;
                        textView.setText(ChangeMusicActivity.formatTimeUnit((long) ChangeMusicActivity.this.duration));
                        if (ChangeMusicActivity.this.is_audio) {
                            mediaPlayer.setVolume(0.0f, 0.0f);
                            ChangeMusicActivity.this.StartAudio();
                            return;
                        }
                        float streamVolume = (float) ((AudioManager) ChangeMusicActivity.this.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(3);
                        mediaPlayer.setVolume(streamVolume, streamVolume);
                        ChangeMusicActivity.this.PauseAudio();
                    } catch (ParseException e) {
                        e.printStackTrace();
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
                        ChangeMusicActivity.this.videoview.seekTo(i);
                        if (ChangeMusicActivity.this.mMediaPlayer != null) {
                            ChangeMusicActivity.this.mMediaPlayer.seekTo(i);
                        }
                    }
                }
            });
            this.videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ChangeMusicActivity.this.videoview.seekTo(100);
                    ChangeMusicActivity.this.btnPlay.setBackgroundResource(R.drawable.play_icn);
                    ChangeMusicActivity.this.videoview.seekTo(0);
                    ChangeMusicActivity.this.seekBar.setProgress(0);
                    ChangeMusicActivity.this.tvStartVideo.setText("00:00");
                    ChangeMusicActivity.this.handler.removeCallbacks(ChangeMusicActivity.this.seekrunnable);
                    ChangeMusicActivity.this.PauseAudio();
                }
            });
        }
    }

    public void Music_Click(View view) {
        StopCustomAudio();
        startActivityForResult(new Intent(this, MusicListActivity.class), 101);
    }

    public void Create() {
        boolean z = false;
        if (this.SelectedAudioUri != null) {
            PauseVideo();
            MediaPlayer create = MediaPlayer.create(this, this.SelectedAudioUri);
            this.audio_duration = create.getDuration();
            create.release();
            Log.e("audio_duration:", "" + this.audio_duration);
            if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                z = true;
            }
            Log.e("Permission:", "" + z);
            SaveDialog();
            return;
        }
        Toast.makeText(this, "please Select Audio File", Toast.LENGTH_SHORT).show();
    }

    private void SaveDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Wont't able to save converted video?");

        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                if (ChangeMusicActivity.this.duration > ChangeMusicActivity.this.audio_duration) {
                    ChangeMusicActivity.this.joinAudio();
                    return;
                }
                ChangeMusicActivity changeMusicActivity = ChangeMusicActivity.this;
                changeMusicActivity.executeTrimAudioCommand(changeMusicActivity.svalue, String.valueOf(ChangeMusicActivity.this.video_duration));
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
        dialog.requestWindowFeature(1);
        this.loading_dialog.setContentView(R.layout.dialog_video_create_loader);
        this.dialog_txt_loading_process = (TextView) this.loading_dialog.findViewById(R.id.dialog_loading_process);
        this.loading_dialog.show();
        ((RelativeLayout) this.loading_dialog.findViewById(R.id.dialog_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChangeMusicActivity.this.audioRxFFmpegSubscriber != null) {
                    ChangeMusicActivity.this.audioRxFFmpegSubscriber.dispose();
                }
                if (ChangeMusicActivity.this.videoRxFFmpegSubscriber != null) {
                    ChangeMusicActivity.this.videoRxFFmpegSubscriber.dispose();
                }
                ChangeMusicActivity.this.loading_dialog.dismiss();
            }
        });
    }

    private void ExitDialog() {
        final Dialog dialog = new Dialog(this, R.style.TransparentBackground);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_exit);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText("Are You Sure?");
        ((TextView) dialog.findViewById(R.id.dialog_desc)).setText("Won't exit without save!");

        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_confirm)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                ChangeMusicActivity.this.BackScreen();
            }
        });
        ((RelativeLayout) dialog.findViewById(R.id.dialog_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            super.onActivityResult(i, i2, intent);
            if (i == 101 && i2 == -1) {
                if (AppClass.selected_audiopath != null) {
                    this.audio_info_layout.setVisibility(View.VISIBLE);
                    if (this.rangseek_layout.getVisibility() == View.GONE) {
                        this.rangseek_layout.setVisibility(View.VISIBLE);
                    }
                }
                Uri parse = Uri.parse(AppClass.selected_audiopath);
                this.SelectedAudioUri = parse;
                MediaPlayer create = MediaPlayer.create(this, parse);
                this.mMediaPlayer = create;
                int duration2 = create.getDuration() / 1000;
                String time = getTime(duration2);
                this.audio_name.setText(AppClass.selected_audiopath.substring(AppClass.selected_audiopath.lastIndexOf("/") + 1));
                if (this.Videopath != null) {
                    getTime(this.video_duration);
                    this.adtxtstart.setText("00:00");
                    this.adtxtend.setText(time);
                    this.is_audio = true;
                }
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void onPause() {
        super.onPause();
        PauseVideo();
    }


    public String getTime(int i) {
        int i2 = i / 3600;
        int i3 = i % 3600;
        return String.format(TimeModel.ZERO_LEADING_NUMBER_FORMAT, new Object[]{Integer.valueOf(i2)}) + ":" + String.format(TimeModel.ZERO_LEADING_NUMBER_FORMAT, new Object[]{Integer.valueOf(i3 / 60)}) + ":" + String.format(TimeModel.ZERO_LEADING_NUMBER_FORMAT, new Object[]{Integer.valueOf(i3 % 60)});
    }

    public void joinAudio() {
        String path = AppClass.getPath(this, this.SelectedAudioUri);
        File externalFilesDir = getExternalFilesDir(AppClass.folder_name);
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdirs();
        }
        File file = new File(externalFilesDir, "audio.txt");
        this.audioIp = file;
        if (file.exists()) {
            this.audioIp.delete();
        }
        int i = 0;
        while (true) {
            appendAudioLog(String.format("file '%s'", new Object[]{path}));
            if (((float) this.video_duration) * 1000.0f <= ((float) (((long) this.audio_duration) * ((long) i)))) {
                break;
            }
            i++;
        }
        if (path != null) {
            File file2 = new File(externalFilesDir, "main_audio" + ("." + AppClass.substringAfterLast(path, ".")));
            if (file2.exists()) {
                file2.delete();
            }
            this.aud_outputpath = file2.getAbsolutePath();
            String[] strArr = {"ffmpeg", "-f", "concat", "-safe", "0", "-i", this.audioIp.getAbsolutePath(), "-c", "copy", "-preset", "ultrafast", "-ac", ExifInterface.GPS_MEASUREMENT_2D, this.aud_outputpath};
            LoadingDialog();
            this.joinaudioRxFFmpegSubscriber = new JoinAudioRxFFmpegSubscriber();
            RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.joinaudioRxFFmpegSubscriber);
        }
    }

    public void appendAudioLog(String str) {
        if (!this.audioIp.exists()) {
            try {
                this.audioIp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.audioIp, true));
            bufferedWriter.append(str);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void executeTrimAudioCommand(String str, String str2) {
        File externalFilesDir = getExternalFilesDir(AppClass.folder_name + "/.TrimAudio");
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdirs();
        }
        Uri parse = Uri.parse(AppClass.selected_audiopath);
        this.SelectedAudioUri = parse;
        String path = AppClass.getPath(this, parse);
        if (path != null) {
            this.TrimAudioPath = new File(externalFilesDir, "Trim_audio" + ("." + AppClass.substringAfterLast(path, "."))).getAbsolutePath();
            if (new File(this.TrimAudioPath).exists()) {
                new File(this.TrimAudioPath).delete();
            }
            String[] strArr = {"ffmpeg", "-i", path, "-ss", str, "-t", str2, "-preset", "veryfast", "-acodec", "copy", this.TrimAudioPath};
            LoadingDialog();
            this.audioRxFFmpegSubscriber = new AudioRxFFmpegSubscriber();
            RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.audioRxFFmpegSubscriber);
        }
    }

    public void ChangeAudioIntoVideo() {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name);
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        String str = this.Videopath;
        String str2 = this.FinalAudioPath;
        String absolutePath = new File(externalStoragePublicDirectory, ("ChangeMusic_" + String.valueOf(Calendar.getInstance().getTimeInMillis())) + ".mp4").getAbsolutePath();
        this.outputpath = absolutePath;
        String[] strArr = {"ffmpeg", "-i", str, "-i", str2, "-strict", "experimental", "-preset", "veryfast", "-map", "0:v", "-map", "1:a", "-s", "320x240", "-r", "30", "-b", "15496k", "-vcodec", "mpeg4", "-ab", "48000", "-ac", ExifInterface.GPS_MEASUREMENT_2D, "-ar", "22050", "-shortest", absolutePath};
        LoadingDialog();
        this.videoRxFFmpegSubscriber = new VideoRxFFmpegSubscriber();
        RxFFmpegInvoke.getInstance().runCommandRxJava(strArr).subscribe(this.videoRxFFmpegSubscriber);
    }

    private void CreateVideoScreen() {
        Intent intent = new Intent(this, VideoViewActivity.class);
        intent.putExtra("videopath", this.outputpath);
        intent.putExtra("frompage", "Music");
        startActivity(intent);
        finish();
    }

    public void StopCustomAudio() {
        Handler handler2 = this.mphandler;
        if (handler2 != null) {
            handler2.removeCallbacks(this.runnable);
        }
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.is_audio = false;
        }
    }

    public void StartAudio() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(this.updated_audio);
            this.mMediaPlayer.start();
            this.mMediaPlayer.setLooping(true);
            this.is_audio = true;
        }
    }

    /* access modifiers changed from: private */
    public void PauseAudio() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
        }
    }

    public void onResume() {
        super.onResume();

    }

    public void NextScreen() {
        if (this.action_name.equalsIgnoreCase(this.BACK)) {
            BackScreen();
        } else if (this.action_name.equalsIgnoreCase(this.Next)) {
            CreateVideoScreen();
        }
    }

    public void BackScreen() {
        finish();
        StopCustomAudio();
    }

    public void onBackPressed() {
        if (this.SelectedAudioUri != null) {
            ExitDialog();
            return;
        }
        this.action_name = this.BACK;
        BackScreen();
    }

    public class AudioRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public AudioRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(ChangeMusicActivity.this.TAG, "SUCCESS with output : ");
            ChangeMusicActivity.this.loading_dialog.dismiss();
            try {
                if (ChangeMusicActivity.this.mMediaPlayer != null) {
                    ChangeMusicActivity.this.StopCustomAudio();
                    if (ChangeMusicActivity.this.rangseek_layout.getVisibility() == View.VISIBLE) {
                        ChangeMusicActivity.this.rangseek_layout.setVisibility(View.GONE);
                    }
                }
                ChangeMusicActivity.this.is_audio = true;
                ChangeMusicActivity changeMusicActivity = ChangeMusicActivity.this;
                changeMusicActivity.FinalAudioPath = changeMusicActivity.TrimAudioPath;
                ChangeMusicActivity.this.mMediaPlayer = new MediaPlayer();
                ChangeMusicActivity.this.mMediaPlayer.setDataSource(ChangeMusicActivity.this.FinalAudioPath);
                ChangeMusicActivity.this.mMediaPlayer.prepare();
                ChangeMusicActivity.this.ChangeAudioIntoVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onProgress(int i, long j) {
            ChangeMusicActivity.this.dialog_txt_loading_process.setText("Please wait..");
            String str = ChangeMusicActivity.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(ChangeMusicActivity.this.TAG, "process cancel : ");
            ChangeMusicActivity.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(ChangeMusicActivity.change_music_activity, "process cancel");
        }

        public void onError(String str) {
            Log.d(ChangeMusicActivity.this.TAG, "FAILED with output : ");
            EUGeneralClass.ShowErrorToast(ChangeMusicActivity.change_music_activity, "Error:These audio can't work plese try with another audio");
        }
    }

    public class JoinAudioRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public JoinAudioRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(ChangeMusicActivity.this.TAG, "SUCCESS with output : ");
            ChangeMusicActivity.this.loading_dialog.dismiss();
            try {
                if (ChangeMusicActivity.this.mMediaPlayer != null) {
                    ChangeMusicActivity.this.StopCustomAudio();
                    if (ChangeMusicActivity.this.rangseek_layout.getVisibility() == View.VISIBLE) {
                        ChangeMusicActivity.this.rangseek_layout.setVisibility(View.GONE);
                    }
                }
                ChangeMusicActivity.this.is_audio = true;
                ChangeMusicActivity changeMusicActivity = ChangeMusicActivity.this;
                changeMusicActivity.FinalAudioPath = changeMusicActivity.aud_outputpath;
                ChangeMusicActivity.this.mMediaPlayer = new MediaPlayer();
                ChangeMusicActivity.this.mMediaPlayer.setDataSource(ChangeMusicActivity.this.aud_outputpath);
                ChangeMusicActivity.this.mMediaPlayer.prepare();
                ChangeMusicActivity.this.ChangeAudioIntoVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onProgress(int i, long j) {
            ChangeMusicActivity.this.dialog_txt_loading_process.setText("Please wait..");
            String str = ChangeMusicActivity.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(ChangeMusicActivity.this.TAG, "process cancel : ");
            ChangeMusicActivity.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(ChangeMusicActivity.change_music_activity, "process cancel");
        }

        public void onError(String str) {
            Log.d(ChangeMusicActivity.this.TAG, "FAILED with output : ");
            EUGeneralClass.ShowErrorToast(ChangeMusicActivity.change_music_activity, "Error:These audio can't work plese try with another audio");
            ChangeMusicActivity.this.loading_dialog.dismiss();
        }
    }

    public class VideoRxFFmpegSubscriber extends RxFFmpegSubscriber {
        public VideoRxFFmpegSubscriber() {
        }

        public void onFinish() {
            Log.e(ChangeMusicActivity.this.TAG, "SUCCESS with output : ");
            ChangeMusicActivity.this.dialog_txt_loading_process.setText("Done");
            ChangeMusicActivity.this.loading_dialog.dismiss();
            ChangeMusicActivity changeMusicActivity = ChangeMusicActivity.this;
            changeMusicActivity.action_name = changeMusicActivity.Next;

            ChangeMusicActivity.this.NextScreen();

        }

        public void onProgress(int i, long j) {
            TextView textView = ChangeMusicActivity.this.dialog_txt_loading_process;
            textView.setText("Video Creation:: " + i + " %");
            String str = ChangeMusicActivity.this.TAG;
            Log.d(str, "progress : " + i);
        }

        public void onCancel() {
            Log.d(ChangeMusicActivity.this.TAG, "process cancel : ");
            ChangeMusicActivity.this.loading_dialog.dismiss();
            EUGeneralClass.ShowErrorToast(ChangeMusicActivity.change_music_activity, "Error:These video can't be modify plese try eith another video");
        }

        public void onError(String str) {
            Log.d(ChangeMusicActivity.this.TAG, "FAILED with output : ");
        }
    }
}