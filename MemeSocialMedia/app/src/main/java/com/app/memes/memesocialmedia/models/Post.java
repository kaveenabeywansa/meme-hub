package com.app.memes.memesocialmedia.models;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("_id")
    private String id;
    @SerializedName("author")
    private String author;
    @SerializedName("text")
    private String text;
    @SerializedName("imageLink")
    private String imageLink;
    @SerializedName("likes")
    private int likes;

    public Post() {

    }

    public Post(String id, String author, String text, String imageLink, int likes) {
        this.id = id;
        this.author = author;
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
        return author;
    }

    public void setDateTime(String author) {
        this.author = author;
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