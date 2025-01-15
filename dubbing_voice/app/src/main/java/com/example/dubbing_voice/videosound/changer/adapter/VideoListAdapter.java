package com.example.dubbing_voice.videosound.changer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.classes.VideoListData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ItemViewHolder> {
    private Context mContext;
    ArrayList<VideoListData> mList = new ArrayList<>();
    ArrayList<VideoListData> searcharray;

    public VideoListAdapter(Context context, ArrayList<VideoListData> arrayList) {
        this.mContext = context;
        this.mList = arrayList;
        ArrayList<VideoListData> arrayList2 = new ArrayList<>();
        this.searcharray = arrayList2;
        arrayList2.addAll(this.mList);
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_list, viewGroup, false));
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        String str = this.mList.get(i).video_name;
        String str2 = this.mList.get(i).video_Url;
        String formatTimeUnit = formatTimeUnit(this.mList.get(i).video_duration);
        itemViewHolder.video_title.setText(str);
        itemViewHolder.video_duration.setText(formatTimeUnit);
        RequestManager with = Glide.with(this.mContext);
        with.load("file://" + str2).apply((BaseRequestOptions<?>) new RequestOptions().placeholder((int) R.drawable.video_thumb)).into(itemViewHolder.thumb);
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView video_duration;
        TextView video_title;

        public ItemViewHolder(View view) {
            super(view);
            this.video_title = (TextView) view.findViewById(R.id.video_title);
            this.video_duration = (TextView) view.findViewById(R.id.video_duration);
            this.thumb = (ImageView) view.findViewById(R.id.thumb);
        }
    }

    public void filter(String str) {
        String lowerCase = str.toLowerCase(Locale.getDefault());
        this.mList.clear();
        if (lowerCase.length() == 0) {
            this.mList.addAll(this.searcharray);
        } else {
            Iterator<VideoListData> it = this.searcharray.iterator();
            while (it.hasNext()) {
                VideoListData next = it.next();
                if (next.video_name.toLowerCase(Locale.getDefault()).contains(lowerCase)) {
                    this.mList.add(next);
                }
            }
        }
        notifyDataSetChanged();
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
