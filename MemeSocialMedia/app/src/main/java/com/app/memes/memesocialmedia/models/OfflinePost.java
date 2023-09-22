package com.app.memes.memesocialmedia.models;

public class OfflinePost {
    private Post post;
    private byte[] postImage;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public byte[] getPostImage() {
        return postImage;
    }

    public void setPostImage(byte[] postImage) {
        this.postImage = postImage;
    }
}
