package com.example.movieplayer2.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.movieplayer2.IMusicPlayService;
import com.example.movieplayer2.domain.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayService extends Service {

    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

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
    };





    private ArrayList<MediaItem> mediaItems;
    private MediaPlayer mediaPlayer;

    private int position;
    private MediaItem mediaItem;

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
                Toast.makeText(MusicPlayService.this, "音频还未准备好", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
        }
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
    }

    /**
     * 暂停音频
     */
    private void pause() {
        mediaPlayer.pause();
    }

    /**
     * 得到演唱者
     *
     * @return
     */
    private String getArtistName() {
        return "";
    }

    /**
     * 得到歌曲名
     *
     * @return
     */
    private String getAudioName() {
        return "";
    }


    /**
     * 得到歌曲路径
     *
     * @return
     */
    private String getAudioPath() {
        return "";
    }

    /**
     * 得到总时长
     *
     * @return
     */
    private int getDuration() {
        return 0;
    }


    /**
     * 得到当前播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return 0;
    }

    /**
     * 音频拖动
     *
     * @param position
     */
    private void seekTo(int position) {
    }

    /**
     * 播放下一个
     */
    private void next() {
    }

    /**
     * 播放上一个
     */
    private void pre() {
    }


}
