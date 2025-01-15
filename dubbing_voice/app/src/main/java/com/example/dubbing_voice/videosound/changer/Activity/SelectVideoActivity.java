package com.example.dubbing_voice.videosound.changer.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.filter.filter.EditVideoActivity;
import com.example.dubbing_voice.videosound.changer.adapter.VideoAlbumAdapter;
import com.example.dubbing_voice.videosound.changer.adapter.VideoListAdapter;
import com.example.dubbing_voice.videosound.changer.classes.RecyclerItemClickListener;
import com.example.dubbing_voice.videosound.changer.classes.VideoAlbumData;
import com.example.dubbing_voice.videosound.changer.classes.VideoListData;

import java.util.ArrayList;
import java.util.HashSet;

public class SelectVideoActivity extends AppCompatActivity {
    public static Activity selectvideo_activity;
    String Actionname = "";
    VideoAlbumAdapter albumAdapter;
    ImageView img_back;
    VideoAlbumData localAlbum;
    Cursor localCursor;
    HashSet localHashSet;
    ArrayList<VideoAlbumData> mAlbumsList = new ArrayList<>();
    ArrayList<VideoListData> mVideoList = new ArrayList<>();
    LinearLayout progress_layout;
    RelativeLayout rel_ad_layout;
    RecyclerView rv_albumlist;
    RecyclerView rv_videolist;
    SearchView searchview;
    String selected_album_name = "";
    TextView text_title;
    String videoPath;
    VideoListAdapter videolistAdapter;
    VideoListData vlist = new VideoListData();


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_album_list);
        selectvideo_activity = this;
        setview();
    }

    private void setview() {
        this.rv_albumlist = findViewById(R.id.album_list_recycelview);
        this.rv_videolist = findViewById(R.id.video_list_recycelview);
        this.progress_layout = findViewById(R.id.lin_progress);
        this.img_back = findViewById(R.id.img_back);

        this.text_title = findViewById(R.id.text_title);
        this.Actionname = getIntent().getStringExtra("Actionanme");
        new FillAlbumTask().execute();
        RecyclerView recyclerView = this.rv_albumlist;
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemLongClick(View view, int i) {
            }

            public void onItemClick(View view, int i) {
                SelectVideoActivity.this.selected_album_name = SelectVideoActivity.this.mAlbumsList.get(i).album_id.trim();
                new FillVideoTask().execute();
            }
        }));
        RecyclerView recyclerView2 = this.rv_videolist;
        recyclerView2.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemLongClick(View view, int i) {
            }

            public void onItemClick(View view, int i) {
                SelectVideoActivity selectVideoActivity = SelectVideoActivity.this;
                selectVideoActivity.videoPath = selectVideoActivity.mVideoList.get(i).video_Url;
                Log.e("TAG14", "file path" + SelectVideoActivity.this.videoPath);
                String str = SelectVideoActivity.this.Actionname;
                str.hashCode();
                if (str.equals("change")) {
                    SelectVideoActivity selectVideoActivity2 = SelectVideoActivity.this;
                    selectVideoActivity2.ChangeAudioScreen(selectVideoActivity2.videoPath);
                } else if (str.equals("record")) {
                    SelectVideoActivity selectVideoActivity3 = SelectVideoActivity.this;
                    selectVideoActivity3.RecordAudioScreen(selectVideoActivity3.videoPath);
                } else if (str.equals("mute")) {
                    SelectVideoActivity selectVideoActivity4 = SelectVideoActivity.this;
                    selectVideoActivity4.MuteAudioScreen(selectVideoActivity4.videoPath);
                } else if (str.equals("crop")) {
                    SelectVideoActivity selectVideoActivity5 = SelectVideoActivity.this;
                    selectVideoActivity5.SlowMotion(selectVideoActivity5.videoPath);
                } else if (str.equals("video_card_main")) {
                    SelectVideoActivity selectVideoActivity6 = SelectVideoActivity.this;
                    selectVideoActivity6.CropScreen(selectVideoActivity6.videoPath);
                } else if (str.equals("Fast_motion")) {
                    SelectVideoActivity selectVideoActivity7 = SelectVideoActivity.this;
                    selectVideoActivity7.Fastslotion(selectVideoActivity7.videoPath);
                } else if (str.equals("Video_filter")) {
                    SelectVideoActivity selectVideoActivity8 = SelectVideoActivity.this;
                    selectVideoActivity8.FliterScreen(selectVideoActivity8.videoPath);
                } else if (str.equals("Trim_Video")) {
                    SelectVideoActivity selectVideoActivity9 = SelectVideoActivity.this;
                    selectVideoActivity9.TrimScreen(selectVideoActivity9.videoPath);
                }
            }
        }));
        this.img_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SelectVideoActivity.this.onBackPressed();
            }
        });
    }

    public void fillAlbums() {
        try {
            this.mAlbumsList = new ArrayList<>();
            Cursor query = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "bucket_display_name", "bucket_id"}, null, null, "bucket_display_name ASC");
            this.localCursor = query;
            int count = query.getCount();
            this.localHashSet = new HashSet();
            int i = 0;
            do {
                this.localAlbum = new VideoAlbumData();
                this.localCursor.moveToPosition(i);
                if (count != 0) {
                    int columnIndex = this.localCursor.getColumnIndex("_data");
                    int columnIndex2 = this.localCursor.getColumnIndex("bucket_display_name");
                    int columnIndex3 = this.localCursor.getColumnIndex("bucket_id");
                    this.localCursor.getColumnIndex("_id");
                    String string = this.localCursor.getString(columnIndex2);
                    this.localAlbum.album_name = string;
                    this.localAlbum.VideoUrl = this.localCursor.getString(columnIndex);
                    this.localAlbum.album_id = this.localCursor.getString(columnIndex3);
                    this.localAlbum.album_count = getCount(this.localCursor.getString(columnIndex3));
                    if (this.localAlbum.album_count > 0 && this.localHashSet.add(string)) {
                        this.mAlbumsList.add(this.localAlbum);
                    }
                    i++;
                } else {
                    return;
                }
            } while (i < this.localCursor.getCount());
        } catch (Exception e) {
            e.toString();
        }
    }

    @SuppressLint("Range")
    public int getCount(String str) {
        try {
            Cursor query = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, "bucket_id=?", new String[]{str}, null);
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < query.getCount(); i++) {
                query.moveToPosition(i);
                if (query.getString(query.getColumnIndex("_data")).contains(".mp4")) {
                    arrayList.add(query.getString(query.getColumnIndex("_data")));
                }
            }
            return arrayList.size();
        } catch (Exception e) {
            e.toString();
            return 0;
        }
    }

    @SuppressLint("Range")
    public void fillVideoList(String str) {
        try {
            this.mVideoList = new ArrayList<>();
            Cursor query = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "duration"}, "bucket_id=?", new String[]{str}, null);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < query.getCount(); i++) {
                query.moveToPosition(i);
                int columnIndex = query.getColumnIndex("_data");
                this.vlist = new VideoListData();
                arrayList.add(query.getString(columnIndex));
                arrayList2.add(query.getString(columnIndex));
                String substring = query.getString(columnIndex).substring(query.getString(columnIndex).lastIndexOf("/") + 1);
                this.vlist.video_id = String.valueOf(columnIndex);
                this.vlist.video_Url = query.getString(columnIndex);
                this.vlist.video_duration = query.getLong(query.getColumnIndex("duration"));
                this.vlist.video_name = substring;
                query.getString(columnIndex).contains(".mp4");
                this.mVideoList.add(this.vlist);
            }
        } catch (Exception e) {
            e.toString();
        }
    }

    public void ChangeAudioScreen(String str) {
        Intent intent = new Intent(this, ChangeMusicActivity.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void MuteAudioScreen(String str) {
        Intent intent = new Intent(this, MuteVideoActivity.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void FliterScreen(String str) {
        Intent intent = new Intent(this, EditVideoActivity.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void TrimScreen(String str) {
        Intent intent = new Intent(this, VideoCropPreActivity.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void SlowMotion(String str) {
        Intent intent = new Intent(this, Slow_Motion.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void Fastslotion(String str) {
        Intent intent = new Intent(this, Fast_forward.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void RecordAudioScreen(String str) {
        Intent intent = new Intent(this, RecordingActivity.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void CropScreen(String str) {
        Intent intent = new Intent(this, VideotoAudioActivity.class);
        intent.putExtra("Filepath", str);
        startActivity(intent);
        finish();
    }

    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        BackScreen();
    }

    private void BackScreen() {
        if (!this.selected_album_name.equals("")) {
            new FillAlbumTask().execute();
            this.selected_album_name = "";
            return;
        }
        finish();
    }

    private class FillAlbumTask extends AsyncTask<Void, Void, Void> {
        private FillAlbumTask() {
        }

        public void onPreExecute() {
            SelectVideoActivity.this.text_title.setText("SELECT ALBUM");
            SelectVideoActivity.this.progress_layout.setVisibility(View.VISIBLE);
            SelectVideoActivity.this.rv_albumlist.setVisibility(View.GONE);
            SelectVideoActivity.this.rv_videolist.setVisibility(View.GONE);
            super.onPreExecute();
        }

        public Void doInBackground(Void[] voidArr) {
            SelectVideoActivity.this.fillAlbums();
            return null;
        }

        public void onPostExecute(Void voidR) {
            SelectVideoActivity.this.progress_layout.setVisibility(View.GONE);
            SelectVideoActivity.this.rv_videolist.setVisibility(View.GONE);
            SelectVideoActivity.this.rv_albumlist.setVisibility(View.VISIBLE);
            SelectVideoActivity selectVideoActivity = SelectVideoActivity.this;
            selectVideoActivity.albumAdapter = new VideoAlbumAdapter(selectVideoActivity, selectVideoActivity.mAlbumsList);
            SelectVideoActivity.this.rv_albumlist.setHasFixedSize(true);
            SelectVideoActivity.this.rv_albumlist.setLayoutManager(new GridLayoutManager(SelectVideoActivity.this, 3));
            SelectVideoActivity.this.rv_albumlist.setAdapter(SelectVideoActivity.this.albumAdapter);
        }
    }

    private class FillVideoTask extends AsyncTask<Void, Void, Void> {
        private FillVideoTask() {
        }

        public void onPreExecute() {
            SelectVideoActivity.this.text_title.setText("SELECT VIDEO");
            SelectVideoActivity.this.progress_layout.setVisibility(View.VISIBLE);
            SelectVideoActivity.this.rv_videolist.setVisibility(View.GONE);
            SelectVideoActivity.this.rv_albumlist.setVisibility(View.GONE);
            super.onPreExecute();
        }

        public Void doInBackground(Void[] voidArr) {
            SelectVideoActivity selectVideoActivity = SelectVideoActivity.this;
            selectVideoActivity.fillVideoList(selectVideoActivity.selected_album_name);
            return null;
        }

        public void onPostExecute(Void voidR) {
            SelectVideoActivity.this.progress_layout.setVisibility(View.GONE);
            SelectVideoActivity.this.rv_albumlist.setVisibility(View.GONE);
            SelectVideoActivity.this.rv_videolist.setVisibility(View.VISIBLE);
            SelectVideoActivity selectVideoActivity = SelectVideoActivity.this;
            selectVideoActivity.videolistAdapter = new VideoListAdapter(selectVideoActivity, selectVideoActivity.mVideoList);
            SelectVideoActivity.this.rv_videolist.setHasFixedSize(true);
            SelectVideoActivity.this.rv_videolist.setLayoutManager(new GridLayoutManager(SelectVideoActivity.this, 3));
            SelectVideoActivity.this.rv_videolist.setAdapter(SelectVideoActivity.this.videolistAdapter);
        }
    }
}

