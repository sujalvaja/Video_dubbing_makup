package com.example.dubbing_voice.videosound.changer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubbing_voice.R;
import com.example.dubbing_voice.videosound.changer.classes.VideoAlbumData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class VideoAlbumAdapter extends RecyclerView.Adapter<VideoAlbumAdapter.ItemViewHolder> {
    private Context mContext;
    ArrayList<VideoAlbumData> mList = new ArrayList<>();
    private ArrayList<VideoAlbumData> searcharray;

    public VideoAlbumAdapter(Context context, ArrayList<VideoAlbumData> arrayList) {
        this.mContext = context;
        this.mList = arrayList;
        ArrayList<VideoAlbumData> arrayList2 = new ArrayList<>();
        this.searcharray = arrayList2;
        arrayList2.addAll(this.mList);
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_album_raw, viewGroup, false));
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        String str = this.mList.get(i).album_name;
        int i2 = this.mList.get(i).album_count;
        itemViewHolder.txt_title.setText(str);
        TextView textView = itemViewHolder.txt_count;
        textView.setText("(" + i2 + ")");
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView img_thumb;
        TextView txt_count;
        TextView txt_title;

        public ItemViewHolder(View view) {
            super(view);
            this.txt_title = (TextView) view.findViewById(R.id.album_row_txt_title);
            this.txt_count = (TextView) view.findViewById(R.id.album_row_item_count);
            this.img_thumb = (ImageView) view.findViewById(R.id.album_row_img_thumb);
            this.txt_title.setSelected(true);
            this.txt_title.setSingleLine(true);
        }
    }

    public void filter(String str) {
        String lowerCase = str.toLowerCase(Locale.getDefault());
        this.mList.clear();
        if (lowerCase.length() == 0) {
            this.mList.addAll(this.searcharray);
        } else {
            Iterator<VideoAlbumData> it = this.searcharray.iterator();
            while (it.hasNext()) {
                VideoAlbumData next = it.next();
                if (next.album_name.toLowerCase(Locale.getDefault()).contains(lowerCase)) {
                    this.mList.add(next);
                }
            }
        }
        notifyDataSetChanged();
    }
}
