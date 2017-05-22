package com.example.movieplayer2.pager;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.movieplayer2.Activity.LocalVideoPlayerActivity;
import com.example.movieplayer2.Adapter.NetVideoAdapter;
import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MovieInfo;
import com.example.movieplayer2.fragment.BaseFragment;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class NetVideoPager extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private NetVideoAdapter adapter;
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_net_video_pager,null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfo.TrailersBean item = adapter.getItem(position);
                Intent intent = new Intent(context, LocalVideoPlayerActivity.class);
                intent.setDataAndType(Uri.parse(item.getUrl()),"video/*");
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
        final RequestParams request =new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "联网成功");
                setData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","联网失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setData(String result) {
        MovieInfo moveInfo = new Gson().fromJson(result, MovieInfo.class);
        List<MovieInfo.TrailersBean> trailers = moveInfo.getTrailers();
        if(trailers!= null && trailers.size()>0) {
            tv_nodata.setVisibility(View.GONE);
            adapter = new NetVideoAdapter(context,trailers);
            lv.setAdapter(adapter);
        }else {
            tv_nodata.setVisibility(View.VISIBLE);
        }
    }


}
