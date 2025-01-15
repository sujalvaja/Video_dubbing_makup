package com.example.dubbing_voice.videosound.changer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.Activity.TimeModel;
import com.example.dubbing_voice.videosound.changer.AppClass;
import com.example.dubbing_voice.videosound.changer.Activity.MusicListActivity;
import com.example.dubbing_voice.videosound.changer.classes.RingtoneClass;
import java.util.List;

public class RingtoneAdapter extends RecyclerView.Adapter<RingtoneAdapter.ItemViewHolder> {
    public static List<RingtoneClass> audio_array;
    Context mcontext;

    public RingtoneAdapter(Context context, List<RingtoneClass> list) {
        audio_array = list;
        this.mcontext = context;
    }

    public int getItemCount() {
        return audio_array.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView audio_duration;
        TextView audio_name;
        RelativeLayout menu_item_card_view;
        RadioButton btnPlayVideo;

        public ItemViewHolder(View view) {
            super(view);
            this.audio_name = (TextView) view.findViewById(R.id.audio_name);
            this.audio_duration = (TextView) view.findViewById(R.id.audio_duration);
             this.menu_item_card_view = (RelativeLayout) view.findViewById(R.id.menu_item_card_view);
             this.btnPlayVideo = (RadioButton) view.findViewById(R.id.btnPlayVideo);
        }
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_audio_raw, viewGroup, false));
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        final RingtoneClass ringtoneClass = audio_array.get(i);
        itemViewHolder.audio_name.setText(ringtoneClass.audio_title);
        itemViewHolder.audio_duration.setText(getTime(ringtoneClass.audio_duration / 1000));
        itemViewHolder.btnPlayVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {

                    buttonView.getId();
                    String str = ringtoneClass.audio_path;
                    ((MusicListActivity) RingtoneAdapter.this.mcontext).setResult(-1, new Intent());
                    AppClass.selected_audiopath = str;
                    ((MusicListActivity) RingtoneAdapter.this.mcontext).finish();
                } else {


                }
                buttonView.getId();
                String str = ringtoneClass.audio_path;
                ((MusicListActivity) RingtoneAdapter.this.mcontext).setResult(-1, new Intent());
                AppClass.selected_audiopath = str;
                ((MusicListActivity) RingtoneAdapter.this.mcontext).finish();
            }
        });
        itemViewHolder.menu_item_card_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
    }

    private String getTime(int i) {
        int i2 = i / 3600;
        int i3 = i % 3600;
        return String.format(TimeModel.ZERO_LEADING_NUMBER_FORMAT, new Object[]{Integer.valueOf(i2)}) + ":" + String.format(TimeModel.ZERO_LEADING_NUMBER_FORMAT, new Object[]{Integer.valueOf(i3 / 60)}) + ":" + String.format(TimeModel.ZERO_LEADING_NUMBER_FORMAT, new Object[]{Integer.valueOf(i3 % 60)});

    }
}
