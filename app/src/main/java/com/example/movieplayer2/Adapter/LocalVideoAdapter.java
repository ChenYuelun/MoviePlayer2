package com.example.movieplayer2.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MediaItem;
import com.example.movieplayer2.utils.Utils;

import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class LocalVideoAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<MediaItem> datas;
    private final boolean isVideo;
    private Utils utils;

    public LocalVideoAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean b) {
        this.context = context;
        this.datas = mediaItems;
        utils = new Utils();
        this.isVideo = b;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 :datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.item_local_video,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name33);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem item = datas.get(position);
        viewHolder.tv_name.setText(item.getName());
        viewHolder.tv_duration.setText(utils.stringForTime((int) item.getDuration()));
        viewHolder.tv_size.setText(Formatter.formatFileSize(context,item.getSize()));
        if(!isVideo){
            //音乐
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }


        return convertView;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;

    }
}
