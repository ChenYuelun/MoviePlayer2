package com.example.movieplayer2.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MediaItem;
import com.example.movieplayer2.utils.Utils;
import com.example.movieplayer2.view.VideoView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LocalVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROCESS = 1;
    private static final int NEWTIME = 99;
    private static final int HIDE_MEDIACONTROLLER = 2;
    private VideoView vv;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwitchScreen;
    private Utils utils;
    private MyBroadCastReceiver receiver;
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private Uri uri;
    private GestureDetector detector;

    private boolean isFullScreen = false;

    private int screenWidth;
    private int screenHeight;

    private int videoWidth;
    private int videoHeight;
    private final  int DEFUALT_SCREEN =3;
    private final  int FULL_SCREEN = 4;

    private int currentVoice;
    private AudioManager am;
    private int maxVoice;
    private boolean isMute = false;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-20 11:01:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_local_video_player);
        vv = (VideoView) findViewById(R.id.vv);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);

        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESS:
                    int currentPosition = vv.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    handler.sendEmptyMessageDelayed(PROCESS, 1000);

                    break;

                case NEWTIME:
                    tvSystemTime.setText(getSystemTime());
                    handler.sendEmptyMessageDelayed(NEWTIME, 60000);
                    break;

                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;

            }

        }
    };

    /**
     * 得到系统时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        handler.sendEmptyMessage(NEWTIME);
        return format.format(new Date());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-20 11:01:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;
            updataVoice(isMute);
            // Handle clicks for btnVoice
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnPre) {
            playPreVideo();
            // Handle clicks for btnPre
        } else if (v == btnStartPause) {
            VideoPlayOrPause();
            // Handle clicks for btnStartPause
        } else if (v == btnNext) {
            playNextVideo();
            // Handle clicks for btnNext
        } else if (v == btnSwitchScreen) {
            SetScreenType();
            // Handle clicks for btnSwitchScreen
        }
    }

    private void updataVoice(boolean isMute) {
        if(isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
            seekbarVoice.setProgress(currentVoice);

        }
    }

    private void SetScreenType() {
        if(isFullScreen) {
            setVideoType(DEFUALT_SCREEN);
        }else {
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int VideoType) {
        switch (VideoType) {
            case DEFUALT_SCREEN:
                isFullScreen = false;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                vv.setVideoSize(width, height);
                break;
            case FULL_SCREEN:
                isFullScreen = true;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                vv.setVideoSize(screenWidth, screenHeight);

                break;
        }

    }

    private void VideoPlayOrPause() {
        if (vv.isPlaying()) {
            vv.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            vv.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getVideoData();
        setVideoData();
        setListener();


        // vv.setMediaController(new MediaController(this));
    }

    private void setVideoData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            vv.setVideoPath(mediaItem.getData());
        } else {
            if (uri != null) {
                vv.setVideoURI(uri);
            }
        }
        setButtonStatus();
    }

    private void getVideoData() {

        Intent intent = getIntent();
        uri = intent.getData();
        mediaItems = (ArrayList<MediaItem>) intent.getSerializableExtra("videoList");
        position = intent.getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();
        receiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                VideoPlayOrPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                SetScreenType();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(isShowMediaController) {
                    hideMediaController();
                    isShowMediaController = false;
                    handler.removeMessages(HIDE_MEDIACONTROLLER);

                }else {
                    showMediaController();
                    isShowMediaController =true;
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                }

                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的宽和高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


    }

    private float startY;
    private float newY;
    private float touchRang;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                startY = event.getY();
                touchRang =Math.min(screenWidth, screenHeight);//screenHeight
                currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;

            case MotionEvent.ACTION_MOVE :
                newY = event.getY();
                float distanceY = startY - newY;
                float voice = (distanceY/touchRang)*maxVoice;
                float changVoice = Math.min(Math.max(voice+currentVoice,0),maxVoice);
                if(changVoice != 0) {
                    updataVoiceProgress((int) changVoice);
                }

                break;
            case MotionEvent.ACTION_UP :
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
        return super.onTouchEvent(event);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updataVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }else if(keyCode ==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updataVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isShowMediaController;
    private void  hideMediaController(){
        llBottom.setVisibility(View.GONE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = false;
    }
    private void  showMediaController(){
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBatteryData(level);

        }
    }

    private void setBatteryData(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();

                int duration = vv.getDuration();
                seekbarVideo.setMax(duration);
                tvDuration.setText(utils.stringForTime(duration));

                vv.start();
                handler.sendEmptyMessage(PROCESS);
                isShowMediaController =true;
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);


            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(LocalVideoPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //如果有下一个 播放下一个
                playNextVideo();
            }
        });

        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    vv.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            }
        });


        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    updataVoiceProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_MEDIACONTROLLER);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);

            }
        });

    }

    private void updataVoiceProgress(int progress) {
        currentVoice = progress;
        //真正改变声音
        am.setStreamVolume(AudioManager.STREAM_MUSIC,currentVoice,0);
        //改变进度条
        seekbarVoice.setProgress(currentVoice);
        if(currentVoice <=0){
            isMute = true;
        }else {
            isMute = false;
        }

    }

    private void playPreVideo() {
        position--;
        if(position>=0) {
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        }
    }

    private void playNextVideo() {
        position++;
        if(position<mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(position);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        }else {
            Toast.makeText(LocalVideoPlayerActivity.this, "播放列表已播放完毕", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setButtonStatus() {
        if(mediaItems!= null&& mediaItems.size()>0) {
            setEnable(true);
            if(position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }
            if(position == mediaItems.size()-1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }
        }else if(uri != null) {
            setEnable(false);
        }
    }

    private void setEnable(boolean b) {
        if(b) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        }else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }

        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }
}
