package com.example.movieplayer2.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by chenyuelun on 2017/5/21.
 */

public class VideoView extends android.widget.VideoView {
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }


    public void setVideoSize(int width,int height){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }
}
