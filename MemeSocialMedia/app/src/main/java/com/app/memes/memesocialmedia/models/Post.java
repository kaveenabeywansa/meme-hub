package com.app.memes.memesocialmedia.models;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("_id")
    private String id;
    @SerializedName("dateTime")
    private String dateTime;
    @SerializedName("text")
    private String text;
    @SerializedName("imageLink")
    private String imageLink;
    @SerializedName("likes")
    private int likes;

    public Post() {

    }

    public Post(String id, String dateTime, String text, String imageLink, int likes) {
        this.id = id;
        this.dateTime = dateTime;
        this.text = text;
        this.imageLink = imageLink;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}