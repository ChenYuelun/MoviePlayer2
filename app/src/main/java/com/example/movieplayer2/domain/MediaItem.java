package com.example.movieplayer2.domain;

import java.io.Serializable;

/**
 * Created by chenyuelun on 2017/5/19.
 */

public class MediaItem implements Serializable {
    private String artist;
    private String content;
    private String icon;
    private String name;
    private long duration;
    private long size;
    private String data;

    public MediaItem(String name, long duraition, long size, String data, String icon, String content) {
        this.name = name;
        this.duration = duraition;
        this.size = size;
        this.data = data;
        this.icon = icon;
        this.content =content;
    }

    public MediaItem(String name, long duration, long size, String data, String artist) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public MediaItem(String name, long duration, long size, String data) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.icon = null;
        this.content = null;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }
}
