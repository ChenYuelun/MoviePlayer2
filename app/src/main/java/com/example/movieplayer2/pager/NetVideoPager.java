package com.example.movieplayer2.pager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayer2.Activity.LocalVideoPlayerActivity;
import com.example.movieplayer2.Adapter.NetVideoAdapter;
import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MediaItem;
import com.example.movieplayer2.domain.MovieInfo;
import com.example.movieplayer2.fragment.BaseFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class NetVideoPager extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private NetVideoAdapter adapter;
    private ArrayList<MediaItem> mediaItems;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,LocalVideoPlayerActivity.class);
                if(mediaItems != null && mediaItems.size()>0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videoList",mediaItems);
                    intent.putExtra("position",position);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void initDatas() {
        super.initDatas();
        getDataFromNet();

    }

    private void getDataFromNet() {
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "联网成功");
                setData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "联网失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setData(String json) {
        mediaItems = new ArrayList<>();
        Log.e("TAGT","将要解析数据");
        try {
            Log.e("TAGT","开始解析数据");
            JSONObject jsonObject = new JSONObject(json);
            String json2 = jsonObject.getString("trailers");
            JSONArray array = new JSONArray(json2);
            for (int i = 0; i < array.length();i++) {
                Log.e("TAGT",""+ i);
                JSONObject jsonObject2 = array.getJSONObject(i);
                String name = jsonObject2.getString("movieName");
                long duraition = jsonObject2.getInt("videoLength") * 1000;
                long size = 0;
                String data = jsonObject2.getString("url");
                String icon = jsonObject2.getString("coverImg");
                String content = jsonObject2.getString("videoTitle");
                MediaItem item = new MediaItem(name,duraition,size,data,icon,content);
                Log.e("TAGT",name +"," + "icon");
                Log.e("TAGT",item.toString());
                mediaItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        if(mediaItems!= null && mediaItems.size() > 0) {
            Log.e("TAGT","配置适配器");
            tv_nodata.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(context,mediaItems);
            lv.setAdapter(adapter);
        }else {
            tv_nodata.setVisibility(View.VISIBLE);
        }
    }

}
