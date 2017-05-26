package com.example.movieplayer2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import io.vov.vitamio.activity.InitActivity;

/**
 * Created by chenyuelun on 2017/5/26.
 */

public class LyricShowView extends TextView {
    private Paint paint;


    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        paint = new Paint();
        paint.setTextSize(16);
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("没有找到歌词",getWidth()/2,getHeight()/2,paint);
    }
}
