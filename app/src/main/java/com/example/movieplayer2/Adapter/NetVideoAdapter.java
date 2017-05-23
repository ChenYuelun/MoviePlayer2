package com.example.movieplayer2.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

;

import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MediaItem;
import com.example.movieplayer2.utils.Utils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;



public class NetVideoAdapter extends BaseAdapter {
    private Context context;
    private  ArrayList<MediaItem> datas;
    private Utils utils;
    private ImageOptions options;





    public NetVideoAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.datas = mediaItems;
        utils = new Utils();

        options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.drawable.video_default)
                .setLoadingDrawableId(R.drawable.video_default)
                .build();
    }


    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public MediaItem getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MediaItem item = datas.get(position);
        viewHolder.tv_name.setText(item.getName());
        viewHolder.tv_content.setText(item.getContent());
        viewHolder.tv_size.setText(utils.stringForTime((int) item.getDuration()));
        x.image().bind(viewHolder.icon,item.getIcon(),options);


        return convertView;
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_content;
        TextView tv_size;
        ImageView icon;
    }
}
