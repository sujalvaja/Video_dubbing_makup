package com.example.dubbing_voice.videosound.changer.bean;

import android.graphics.Bitmap;


public class LocalVideoBean {
    public Bitmap coverImage;
    public long duration;
    public String src_path;
    public long fileSize;
    public int fps;
    public int bitrate;
    public int width;
    public int height;

    @Override
    public String toString() {
        return "LocalVideoBean{" +
                " duration=" + duration +
                ", src_path='" + src_path + '\'' +
                ", fileSize=" + fileSize +
                ", fps=" + fps +
                ", bitrate=" + bitrate +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
