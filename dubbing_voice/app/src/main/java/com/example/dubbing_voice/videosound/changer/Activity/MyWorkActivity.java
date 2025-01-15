package com.example.dubbing_voice.videosound.changer.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.AppClass;
import com.example.dubbing_voice.videosound.changer.Fragment.Fast_ForwardFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.FilterFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.MuteFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.RecordFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.Slow_MotionFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.TrimFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.Video_to_audioFragment;
import com.example.dubbing_voice.videosound.changer.Fragment.changeaudiofragment;
import com.example.dubbing_voice.videosound.changer.adapter.MyCreationAdapter;
import com.example.dubbing_voice.videosound.changer.classes.VideoData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MyWorkActivity extends AppCompatActivity {

    public static Activity my_work_activity;
    FrameLayout simpleFrameLayout;
    Spinner tabLayout;
    ImageView ic_back;
    TextView lbl_no_data;
    MyCreationAdapter my_work_adapter;
    ArrayList<VideoData> mycreationList = new ArrayList<>();
    SpinKitView progress;
    RecyclerView rv_mywork;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;
    Fragment fragment5;
    Fragment fragment6;
    Fragment fragment7;
    Fragment fragment8;
    String[] courses = { "Change_audio", "mute_audio",
            "Record_audio", "slow_motion",
            "Fast_forward", "Filter_view" ,"Trim_video","Video_to_Audio"};

    public void onCreate(Bundle bundle) {
        supportRequestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        try {
            setContentView((int) R.layout.activity_my_work);
            my_work_activity = this;
            this.progress = (SpinKitView) findViewById(R.id.progress_avi);
            this.lbl_no_data = (TextView) findViewById(R.id.lbl_nodata);
            this.ic_back = (ImageView) findViewById(R.id.img_back);
            simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
            tabLayout = (Spinner) findViewById(R.id.tabMode);
            ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,courses);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tabLayout.setAdapter(aa);
             fragment1 = new changeaudiofragment();
             fragment2 = new MuteFragment();
             fragment3 = new RecordFragment();
             fragment4 = new Slow_MotionFragment();
             fragment5 = new Fast_ForwardFragment();
             fragment6 = new FilterFragment();
             fragment7 = new TrimFragment();
             fragment8 = new Video_to_audioFragment();

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_mywork);
            this.rv_mywork = recyclerView;
            recyclerView.setHasFixedSize(true);
            this.rv_mywork.setLayoutManager(new GridLayoutManager(this, 2));
            this.ic_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    MyWorkActivity.this.onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
       tabLayout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               switch (position) {
                   case 0:
                      selectFragment(fragment1);
                       break;
                   case 1:
                       selectFragment(fragment2);
                       break;
                   case 2:
                       selectFragment(fragment3);
                       break;
                   case 3:
                       selectFragment(fragment4);
                       break;
                   case 4:
                       selectFragment(fragment5);

                       break;
                   case 5:
                       selectFragment(fragment6);

                       break;
                   case 6:
                       selectFragment(fragment7);

                       break;
                   case 7:
                       selectFragment(fragment8);
               }

           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
    }

    private void selectFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.simpleFrameLayout, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }


    public void FillData() {
        if (this.mycreationList.size() > 0) {
            this.mycreationList.clear();
        }
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            BackScreen();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onResume() {
        super.onResume();
        new loadVideoList().execute(new Void[0]);
    }

    public void onBackPressed() {
        super.onBackPressed();
        BackScreen();
    }

    public void BackScreen() {
        finish();
    }
 private class loadVideoList extends AsyncTask<Void, Void, Boolean> {
        private loadVideoList() {
        }


        public void onPreExecute() {
            MyWorkActivity.this.progress.setVisibility(View.VISIBLE);
        }


        public Boolean doInBackground(Void[] voidArr) {
            MyWorkActivity.this.FillData();
            return true;
        }


        public void onPostExecute(Boolean bool) {
            if (MyWorkActivity.this.mycreationList.size() > 0) {
                MyWorkActivity.this.lbl_no_data.setVisibility(View.GONE);
                MyWorkActivity myWorkActivity = MyWorkActivity.this;
                myWorkActivity.my_work_adapter = new MyCreationAdapter(myWorkActivity, myWorkActivity.mycreationList/*,courses*/);
                MyWorkActivity.this.rv_mywork.setAdapter(MyWorkActivity.this.my_work_adapter);
                my_work_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            } else {
                MyWorkActivity.this.lbl_no_data.setVisibility(View.VISIBLE);
            }
            MyWorkActivity.this.progress.setVisibility(View.GONE);
        }
    }
}
