package com.example.movieplayer2.Activity;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.movieplayer2.R;

public class LocalAudioPlayerActivity extends AppCompatActivity {
    private ImageView iv_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_audio_player);

        iv_icon = (ImageView)findViewById(R.id.iv_icon);
        iv_icon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) iv_icon.getBackground();
        background.start();
    }
}
