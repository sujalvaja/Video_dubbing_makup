package com.example.dubbing_voice.videosound.changer.Fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.AppClass;
import com.example.dubbing_voice.videosound.changer.adapter.MyCreationAdapter;
import com.example.dubbing_voice.videosound.changer.classes.VideoData;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Fast_ForwardFragment extends Fragment {
    ImageView ic_back;
    TextView lbl_no_data;
    MyCreationAdapter my_work_adapter;
    ArrayList<VideoData> mycreationList = new ArrayList<>();
    SpinKitView progress;
    RelativeLayout rel_ad_layout;
    RecyclerView rv_mywork;
    public Fast_ForwardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fast__forward, container, false);
        this.progress = view.findViewById(R.id.progress_avi);
        this.lbl_no_data =  view.findViewById(R.id.lbl_nodata);
        this.ic_back = view.findViewById(R.id.img_back);
        RecyclerView recyclerView = view.findViewById(R.id.rv_mywork);
        this.rv_mywork = recyclerView;
        recyclerView.setHasFixedSize(true);
        this.rv_mywork.setLayoutManager(new GridLayoutManager(getContext(), 3));
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private class loadVideoList extends AsyncTask<Void, Void, Boolean> {
        private loadVideoList() {
        }


        public void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }


        public Boolean doInBackground(Void[] voidArr) {
            FillData();
            return true;
        }


        public void onPostExecute(Boolean bool) {
            if (mycreationList.size() > 0) {
                lbl_no_data.setVisibility(View.GONE);
                Fast_ForwardFragment myWorkActivity = Fast_ForwardFragment.this;
                myWorkActivity.my_work_adapter = new MyCreationAdapter(getContext(), myWorkActivity.mycreationList/*, my_work_adapter.courses*/);
                rv_mywork.setAdapter(my_work_adapter);
            } else {
                lbl_no_data.setVisibility(View.VISIBLE);
            }
            progress.setVisibility(View.GONE);
        }
    }


    public void FillData() {
        if (this.mycreationList.size() > 0) {
            this.mycreationList.clear();
        }
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name+"/Fast_Forward");
        if (externalStoragePublicDirectory.exists() && externalStoragePublicDirectory.isDirectory()) {
            String[] list = externalStoragePublicDirectory.list();
            if (list.length != 0) {
                for (int i = 0; i < list.length; i++) {
                    String str = externalStoragePublicDirectory + File.separator + list[i];
                    if (str.contains(".mp4")) {
                        String str2 = list[i];
                        String date = new Date(externalStoragePublicDirectory.lastModified()).toString();
                        VideoData videoData = new VideoData();
                        videoData.videoPath = str;
                        videoData.videoName = str2;
                        videoData.Duration = date;
                        this.mycreationList.add(videoData);
                    }
                }
            }
            Collections.reverse(this.mycreationList);
        }
    }



    public void onResume() {
        super.onResume();
        new loadVideoList().execute(new Void[0]);
    }
}