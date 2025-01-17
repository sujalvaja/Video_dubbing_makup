package com.example.dubbing_voice.videosound.changer.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.VideoView;


public class LocalVideoView extends VideoView implements MediaPlayer.OnCompletionListener {
    private String mFilePath;
    private int startPo;
    private int endPo;

    private boolean isStart = false;

    private Handler mHandler = new Handler();
    private Runnable countTimeRun = new Runnable() {
        public void run() {
            int currentPosition = getCurrentPosition();
            onTimePlaying(currentPosition);
            mHandler.postDelayed(countTimeRun, 1000);
        }
    };

    public LocalVideoView(Context context) {
        super(context);
    }

    public LocalVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocalPath(String path, int endPo) {
        if (!TextUtils.isEmpty(path)) {
            mFilePath = path;
            setVideoPath(path);
            setOnCompletionListener(this);
            start();

            this.startPo = 0;
            this.endPo = endPo;
        }
    }

    public int getStartPo() {
        return startPo;
    }

    public int getEndPo() {
        return endPo;
    }

    public void setStarEndPo(int start, int end) {
        startPo = start;
        endPo = end;
        start0();
    }

    public void start() {
        if (!isStart) {
            isStart = true;
            mHandler.post(countTimeRun);
            seekTo(startPo);
        }
        super.start();
    }

    @Override
    public void pause() {
        if (isStart) {
            isStart = false;
            mHandler.removeCallbacks(countTimeRun);
        }
        super.pause();
    }

    @Override
    public void stopPlayback() {
        if (isStart) {
            isStart = false;
            mHandler.removeCallbacks(countTimeRun);
        }
        super.stopPlayback();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopPlayback();
        super.onDetachedFromWindow();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        start0();
    }

    public void start0() {
        if (isStart) {
            seekTo(startPo);
            start();
        }
    }

    private void onTimePlaying(int time) {
        if (endPo > 0 && time >= endPo) {
            start0();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (onVideoSizeChangeedListener != null) {
            onVideoSizeChangeedListener.onVideoSizeChanged(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    private OnVideoSizeChangeedListener onVideoSizeChangeedListener;

    public void setOnVideoSizeChangeedListener(OnVideoSizeChangeedListener onVideoSizeChangeedListener) {
        this.onVideoSizeChangeedListener = onVideoSizeChangeedListener;
    }


    public interface OnVideoSizeChangeedListener {
        public void onVideoSizeChanged(int width, int height);
    }
}
