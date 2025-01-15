package com.example.dubbing_voice.videosound.changer.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.adapter.RingtoneAdapter;
import com.example.dubbing_voice.videosound.changer.classes.RingtoneClass;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends Activity {
    public static List<RingtoneClass> audio_array;
    public static Activity music_list_activity;
    TextView emty_text;
    GetData getdata;
    ImageView img_back;
    SpinKitView progress_avi;
    RecyclerView recyclerview_audio_list;
    RingtoneAdapter ringtoneAdapter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            setContentView(R.layout.activity_music_list);
            music_list_activity = this;
            setView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView() {
        this.recyclerview_audio_list = (RecyclerView) findViewById(R.id.recyclerview_audio_list);
        this.emty_text = (TextView) findViewById(R.id.emty_text);
        this.progress_avi = (SpinKitView) findViewById(R.id.progress_avi);
        this.img_back = (ImageView) findViewById(R.id.img_back);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        this.recyclerview_audio_list.setHasFixedSize(true);
        this.recyclerview_audio_list.setLayoutManager(gridLayoutManager);
        this.img_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MusicListActivity.this.onBackPressed();
            }
        });
    }


    public ArrayList<RingtoneClass> listAllAudio(Context context) {
        Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "title", "duration", "_size"}, (String) null, (String[]) null, "title DESC");
        ArrayList<RingtoneClass> arrayList = new ArrayList<>();
        arrayList.clear();
        for (int i = 0; i < query.getCount(); i++) {
            query.moveToPosition(i);
            @SuppressLint("Range") String string = query.getString(query.getColumnIndex("_data"));
            if (!string.contains("Android/media")) {
                @SuppressLint("Range") long j = query.getLong(query.getColumnIndex("_id"));
                @SuppressLint("Range") String string2 = query.getString(query.getColumnIndex("title"));
                @SuppressLint("Range") int i2 = query.getInt(query.getColumnIndex("duration"));
                RingtoneClass ringtoneClass = new RingtoneClass();
                ringtoneClass.audio_id = Long.valueOf(j);
                ringtoneClass.audio_title = string2;
                ringtoneClass.audio_duration = i2;
                ringtoneClass.audio_path = string;
                arrayList.add(ringtoneClass);
            }
        }
        query.close();
        return arrayList;
    }

    public void onResume() {
        super.onResume();
        GetData getData = new GetData();
        this.getdata = getData;
        getData.execute(new String[0]);
    }

    public void onBackPressed() {
        super.onBackPressed();
        BackScreen();
    }

    private void BackScreen() {
        finish();
    }

    public class GetData extends AsyncTask<String, Void, String> {
        public GetData() {
        }


        public void onPreExecute() {
            MusicListActivity.this.progress_avi.setVisibility(View.VISIBLE);
            MusicListActivity.this.recyclerview_audio_list.setVisibility(View.GONE);
        }

        public String doInBackground(String... strArr) {
            MusicListActivity musicListActivity = MusicListActivity.this;
            MusicListActivity.audio_array = musicListActivity.listAllAudio(musicListActivity);
            return null;
        }


        public void onPostExecute(String str) {
            MusicListActivity.this.progress_avi.setVisibility(View.GONE);
            MusicListActivity.this.recyclerview_audio_list.setVisibility(View.VISIBLE);
            if (MusicListActivity.audio_array.size() > 0) {
                MusicListActivity.this.recyclerview_audio_list.setVisibility(View.VISIBLE);
                MusicListActivity.this.emty_text.setVisibility(View.GONE);
                MusicListActivity musicListActivity = MusicListActivity.this;
                musicListActivity.ringtoneAdapter = new RingtoneAdapter(musicListActivity, MusicListActivity.audio_array);
                MusicListActivity.this.recyclerview_audio_list.setAdapter(MusicListActivity.this.ringtoneAdapter);
                return;
            }
            MusicListActivity.this.recyclerview_audio_list.setVisibility(View.GONE);
            MusicListActivity.this.emty_text.setVisibility(View.VISIBLE);
        }
    }
}
