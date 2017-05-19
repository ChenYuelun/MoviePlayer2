package com.example.movieplayer2.pager;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.movieplayer2.fragment.BaseFragment;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class NetAudioPager extends BaseFragment {
    private TextView textView;
    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(30);
        return textView;
    }


    @Override
    public void initDatas() {
        super.initDatas();
        textView.setText("这是网络音频界面");

    }
}
