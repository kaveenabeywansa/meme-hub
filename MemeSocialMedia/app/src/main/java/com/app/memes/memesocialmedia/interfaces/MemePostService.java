package com.app.memes.memesocialmedia.interfaces;

import com.app.memes.memesocialmedia.models.Post;
import com.app.memes.memesocialmedia.models.PostsResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MemePostService {
    @GET("posts/")
    Call<PostsResponse> getAll();

    @POST("posts/")
    Call<ResponseBody> addMeme(@Body Post post);

    @POST("posts/upvote/{id}")
    Call<ResponseBody> upVote(@Path("id") String postId);

    @POST("posts/downvote/{id}")
    Call<ResponseBody> downVote(@Path("id") String postId);
}
