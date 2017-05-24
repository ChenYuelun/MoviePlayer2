package com.example.movieplayer2.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.movieplayer2.IMusicPlayService;
import com.example.movieplayer2.R;
import com.example.movieplayer2.service.MusicPlayService;
import com.example.movieplayer2.utils.Utils;

import static android.R.attr.duration;
import static com.example.movieplayer2.R.id.iv_icon;

public class LocalAudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int PROGRESS = 1;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvAudioname;
    private LinearLayout llBottom;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnPlaymode;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnLyric;
    private int position;
    private IMusicPlayService service;
    private int duration;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayService.Stub.asInterface(iBinder);
            if(service != null){
                try {
                    if(notification) {
                        setViewData();
                    }else {

                        service.openAudio(position);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Utils utils;
    private MyReceiver receiver;
    private boolean notification;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-24 17:44:37 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_local_audio_player);
        ivIcon = (ImageView)findViewById( iv_icon );
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvAudioname = (TextView)findViewById( R.id.tv_audioname );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btnPlaymode = (Button)findViewById( R.id.btn_playmode );
        btnPre = (Button)findViewById( R.id.btn_pre );
        btnStartPause = (Button)findViewById( R.id.btn_start_pause );
        btnNext = (Button)findViewById( R.id.btn_next );
        btnLyric = (Button)findViewById( R.id.btn_lyric );

        btnPlaymode.setOnClickListener( this );
        btnPre.setOnClickListener( this );
        btnStartPause.setOnClickListener( this );
        btnNext.setOnClickListener( this );
        btnLyric.setOnClickListener( this );
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS :
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(duration));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };
    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-24 17:44:37 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnPlaymode ) {
            setPlayMode();
            // Handle clicks for btnPlaymode
        } else if ( v == btnPre ) {
            try {
                MusicPlayService.nextFromUser = true;
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnPre
        } else if ( v == btnStartPause ) {
            try {
                if(service.isPlaying()){
                    service.pause();
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                }else{
                    service.start();
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Handle clicks for btnStartPause
        } else if ( v == btnNext ) {
            try {
                MusicPlayService.nextFromUser = true;
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btnNext
        } else if ( v == btnLyric ) {
            // Handle clicks for btnLyric
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlayMOde();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                playmode = MusicPlayService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                playmode = MusicPlayService.REPEAT_ALL;
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                playmode = MusicPlayService.REPEAT_NORMAL;
            }
            //保存到服务里面
            service.setPlayMode(playmode);
            setButtonImage();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void setButtonImage() {
        try {
            //从服务得到播放模式
            int playmode = service.getPlayMOde();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();

        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();

        getData();

        Intent intent = new Intent(this,MusicPlayService.class);
        bindService(intent,conn,BIND_AUTO_CREATE);
        startService(intent);

        initData();
        setListener();

    }

    private void setListener() {
        seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    try {
                        service.seekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initData() {
        utils = new Utils();
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.OPEN_COMPLETE);
        registerReceiver(receiver,filter);
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewData();
        }
    }

    private void setViewData() {
        try {
            tvArtist.setText(service.getArtistName());
            tvAudioname.setText(service.getAudioName());
            duration = service.getDuration();
            seekbarAudio.setMax(duration);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(PROGRESS);

    }

    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if(!notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }
    @Override
    protected void onDestroy() {

        if(conn != null){
            unbindService(conn);
            conn = null;
        }

        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
    }
}
