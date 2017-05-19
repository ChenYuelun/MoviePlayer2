package com.example.movieplayer2;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.movieplayer2.fragment.BaseFragment;
import com.example.movieplayer2.pager.LocalAudioPager;
import com.example.movieplayer2.pager.LocalVideoPager;
import com.example.movieplayer2.pager.NetAudioPager;
import com.example.movieplayer2.pager.NetVideoPager;

import java.util.ArrayList;

import static android.R.attr.fragment;
import static com.example.movieplayer2.R.id.rb_local_audio;
import static com.example.movieplayer2.R.id.rb_local_video;
import static com.example.movieplayer2.R.id.rb_net_audio;
import static com.example.movieplayer2.R.id.rb_net_video;

public class MainActivity extends AppCompatActivity {
    private FrameLayout framlayout;
    private RadioGroup radiogroup;
    private int position;
    private ArrayList<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        framlayout = (FrameLayout) findViewById(R.id.framlayout);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        initFragments();

        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener());
        radiogroup.check(rb_local_video);
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoPager());
        fragments.add(new LocalAudioPager());
        fragments.add(new NetVideoPager());
        fragments.add(new NetAudioPager());
    }

    private class OnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case rb_local_video:
                    position = 0;
                    break;
                case rb_local_audio:
                    position = 1;
                    break;
                case rb_net_video:
                    position = 2;
                    break;
                case rb_net_audio:
                    position = 3;
                    break;
            }
            BaseFragment fragment = fragments.get(position);
            addFragment(fragment);
        }
    }

    private BaseFragment tempFragment;

    private void addFragment(BaseFragment fragment) {
        if (tempFragment != fragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(!fragment.isAdded()) {
                if(tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.add(R.id.framlayout,fragment);
            }else {
                if(tempFragment != null) {
                    ft.hide(tempFragment);
                }
                ft.show(fragment);
            }

            ft.commit();
            tempFragment = fragment;

        }

    }
}
