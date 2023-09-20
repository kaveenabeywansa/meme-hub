package com.app.memes.memesocialmedia.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostsResponse {
    @SerializedName("data")
    @Expose
    private List<Post> postList = null;

    public List<Post> getPostList() {
        return postList;
    }
}
