package com.setayeshco.rooyesh.models.calls;

/**
 * Created by Abderrahim El imame on 6/16/17.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/Ben__Cherif
 * @Skype : ben-_-cherif
 */

public class CallSaverModel {

    private int fromId;
    private int toId;
    private String date;
    private String duration;
    private String isVideo;

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(String isVideo) {
        this.isVideo = isVideo;
    }
}
