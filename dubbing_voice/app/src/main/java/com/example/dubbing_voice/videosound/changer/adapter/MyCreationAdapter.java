package com.example.dubbing_voice.videosound.changer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.Activity.VideoViewActivity;
import com.example.dubbing_voice.videosound.changer.classes.VideoData;
import java.util.ArrayList;

public class MyCreationAdapter extends RecyclerView.Adapter<MyCreationAdapter.ItemViewHolder>  {
    ArrayList<VideoData> VideoList = new ArrayList<>();
    public Context mContext;
    public MyCreationAdapter(Context context, ArrayList<VideoData> arrayList/*, String[] courses*/) {
        this.mContext = context;
        this.VideoList = arrayList;
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mywork_raw, viewGroup, false));
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, @SuppressLint("RecyclerView") final int i) {
        String str = this.VideoList.get(i).videoName;
        final String str2 = this.VideoList.get(i).videoPath;
        int i2 = 0;
        try {
            MediaPlayer create = MediaPlayer.create(this.mContext, Uri.parse(str2));
            i2 = create.getDuration();
            create.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String formatTimeUnit = formatTimeUnit((long) i2);
        itemViewHolder.video_title.setText(str);
        itemViewHolder.video_duration.setText(formatTimeUnit);
        RequestManager with = Glide.with(this.mContext);
        with.load("file://" + str2).apply((BaseRequestOptions<?>) new RequestOptions().placeholder((int) R.drawable.video_thumb)).into(itemViewHolder.thumb);
        itemViewHolder.main_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MyCreationAdapter.this.CreateVideoScreen(i);
            }
        });
        itemViewHolder.share_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("video/mp4");
                    intent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + str2));
                    MyCreationAdapter.this.mContext.startActivity(Intent.createChooser(intent, "Compare"));
                } catch (Exception e) {
                    e.toString();
                }
            }
        });
    }

    public int getItemCount() {
        return this.VideoList.size();
    }

    public void setDropDownViewResource(int simple_spinner_dropdown_item) {
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView main_layout;
        LinearLayout share_layout;
        ImageView thumb;
        TextView video_duration;
        TextView video_title;

        public ItemViewHolder(View view) {
            super(view);
            this.share_layout = (LinearLayout) view.findViewById(R.id.share_layout);
            this.main_layout = (CardView) view.findViewById(R.id.video_card_main);
            this.video_title = (TextView) view.findViewById(R.id.video_title);
            this.video_duration = (TextView) view.findViewById(R.id.video_duration);
            this.thumb = (ImageView) view.findViewById(R.id.thumb);
        }
    }

    
    public void CreateVideoScreen(int i) {
        Intent intent = new Intent(this.mContext, VideoViewActivity.class);
        intent.putExtra("videopath", this.VideoList.get(i).videoPath);
        intent.putExtra("frompage", "Folder");
        this.mContext.startActivity(intent);
    }

    public static String formatTimeUnit(long j) {
        Object obj;
        Object obj2;
        Object obj3;
        int i = ((int) (j / 1000)) % 60;
        int i2 = ((int) (j / 60000)) % 60;
        long j2 = (long) (((int) (j / 3600000)) % 24);
        if (j2 < 10) {
            obj = "0" + j2;
        } else {
            obj = Long.valueOf(j2);
        }
        String valueOf = String.valueOf(obj);
        if (i2 < 10) {
            obj2 = "0" + i2;
        } else {
            obj2 = Integer.valueOf(i2);
        }
        String valueOf2 = String.valueOf(obj2);
        if (i < 10) {
            obj3 = "0" + i;
        } else {
            obj3 = Integer.valueOf(i);
        }
        return valueOf + ":" + valueOf2 + ":" + String.valueOf(obj3);
    }
}
