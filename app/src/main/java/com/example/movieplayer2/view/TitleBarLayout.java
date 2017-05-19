package com.example.movieplayer2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieplayer2.R;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class TitleBarLayout extends LinearLayout implements View.OnClickListener {
    private TextView searchBox;
    private RelativeLayout ll_game;
    private ImageView record;
    private Context context;
    public TitleBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        searchBox = (TextView) getChildAt(1);
        ll_game = (RelativeLayout) getChildAt(2);
        record = (ImageView) getChildAt(3);


        searchBox.setOnClickListener(this);
        ll_game.setOnClickListener(this);
        record.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBox :
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();

                break;
            case R.id.ll_game :
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.record :
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
