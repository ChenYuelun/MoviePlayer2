package com.example.movieplayer2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.movieplayer2.Activity.LocalAudioPlayerActivity;
import com.example.movieplayer2.IMusicPlayService;
import com.example.movieplayer2.R;
import com.example.movieplayer2.domain.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.action;

public class MusicPlayService extends Service {

    public static final String OPEN_COMPLETE = "com.atguigu.mobileplayer.OPEN_COMPLETE";
    private NotificationManager nm;
    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void setPlayMode(int PlayMode) throws RemoteException {
            service.setPlayMode(PlayMode);
        }

        @Override
        public int getPlayMOde() throws RemoteException {
            return service.getPlayMode();
        }
    };





    private ArrayList<MediaItem> mediaItems;
    private MediaPlayer mediaPlayer;

    public int position;
    private MediaItem mediaItem;
    public static boolean nextFromUser = false;


    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;

    /**
     * 单曲循环播放
     */
    public static final int REPEAT_SINGLE = 2;

    /**
     * 全部循环播放
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;

    public MusicPlayService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getLocalAudioData();
    }

    private void getLocalAudioData() {
        new Thread(){
            public void run(){
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null) {
                    while(cursor.moveToNext()) {
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        if(duration > 10*1000) {
                            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            Log.e("TAG", "Service_name==" + name + ",duration==" + duration + ",data===" + data+",artist=="+artist);
                            mediaItems.add(new MediaItem(name,duration,size,data,artist));

                        }



                    }
                    cursor.close();

                }

            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


    /**
     * 根据位置播放一个音频
     *
     * @param position
     */
    private void openAudio(int position) {
        this.position = position;
        if(mediaItems!= null && mediaItems.size() > 0) {
            if(position < mediaItems.size()) {
                mediaItem = mediaItems.get(position);

                if(mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer = null;

                }

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(mediaItem.getData());
                    mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                    mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                    mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }else {
                Toast.makeText(MusicPlayService.this, "已到最后一首", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyChange(OPEN_COMPLETE);
            start();
        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);

    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            
            next();
        }
    }


    /**
     * 播放音频
     */
    private void start() {
        mediaPlayer.start();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, LocalAudioPlayerActivity.class);
        intent.putExtra("notification",true);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notifation = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("我的音乐")
                .setContentText("正在播放："+getAudioName())
                .setContentIntent(pi)
                .build();
         nm.notify(1,notifation);
    }

    /**
     * 暂停音频
     */
    private void pause() {
        mediaPlayer.pause();
        nm.cancel(1);
    }

    /**
     * 得到演唱者
     *
     * @return
     */
    private String getArtistName() {
        return mediaItem.getArtist();
    }

    /**
     * 得到歌曲名
     *
     * @return
     */
    private String getAudioName() {
        return mediaItem.getName();
    }


    /**
     * 得到歌曲路径
     *
     * @return
     */
    private String getAudioPath() {
        return mediaItem.getData();
    }

    /**
     * 得到总时长
     *
     * @return
     */
    private int getDuration() {
        return mediaPlayer.getDuration();
    }


    /**
     * 得到当前播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 音频拖动
     *
     * @param position
     */
    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    /**
     * 播放下一个
     */
    private void next() {

        if(playmode != REPEAT_SINGLE || nextFromUser) {
            position++;
            nextFromUser = false;
            if(position > mediaItems.size()-1) {
                if(playmode == REPEAT_NORMAL || playmode == REPEAT_SINGLE) {
                    position = mediaItems.size()-1;
                    Toast.makeText(MusicPlayService.this, "已到最后一首", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playmode == REPEAT_ALL) {
                    position = 0;
                }
            }
        }
        openAudio(position);
    }



    /**
     * 播放上一个
     */
    private void pre() {

        if(playmode != REPEAT_SINGLE || nextFromUser) {
            position--;
            nextFromUser = false;
            if(position < 0) {
                if(playmode == REPEAT_NORMAL || playmode == REPEAT_SINGLE ) {
                    position = 0;
                    Toast.makeText(MusicPlayService.this, "已是第一首", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(playmode == REPEAT_ALL) {
                    position = mediaItems.size() - 1;
                }
            }

        }
        openAudio(position);
    }

    private int getPlayMode(){
        return playmode;
    }
    private void setPlayMode(int playmode){
        this.playmode = playmode;
    }
}
