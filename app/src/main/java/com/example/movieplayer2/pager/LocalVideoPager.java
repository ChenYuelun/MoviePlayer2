package com.example.movieplayer2.pager;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayer2.Activity.LocalVideoPlayerActivity;
import com.example.movieplayer2.Adapter.LocalVideoAdapter;
import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MediaItem;
import com.example.movieplayer2.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class LocalVideoPager extends BaseFragment {
    private ListView lv_local_video;
    private TextView tv_nodata;
    private ArrayList<MediaItem> mediaItems;
    private LocalVideoAdapter adapter;
    
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.local_video_item,null);
        lv_local_video = (ListView) view.findViewById(R.id.lv_local_video);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv_local_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(Intent.ACTION_VIEW);
//                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(mediaItems.get(position).getData()),"video/*");
//                startActivity(intent);
                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoList",mediaItems);
                intent.putExtra("position",position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        
        return view;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaItems != null && mediaItems.size() > 0) {
                tv_nodata.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(context,mediaItems, true);
                lv_local_video.setAdapter(adapter);

            }else {
                tv_nodata.setVisibility(View.VISIBLE);
            }


        }
    };


    @Override
    public void initDatas() {
        super.initDatas();
        getLocalVideoData();
    }

    private void getLocalVideoData() {
        new Thread(){
            public void run(){
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null) {
                    while(cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.e("TAG","name=" + name);
                        mediaItems.add(new MediaItem(name,duration,size,data));


                    }
                    cursor.close();
                    handler.sendEmptyMessage(1);



                }

            }
        }.start();
    }


}
