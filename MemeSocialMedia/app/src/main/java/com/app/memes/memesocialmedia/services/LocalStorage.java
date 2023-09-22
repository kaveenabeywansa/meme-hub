package com.app.memes.memesocialmedia.services;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.memes.memesocialmedia.CreatePost;
import com.app.memes.memesocialmedia.models.OfflinePost;
import com.app.memes.memesocialmedia.models.Post;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalStorage {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private String SHARED_PREF_NAME = "MEME_HUB";
    private String OFFLINE_QUEUE_STR = "OFFLINE_QUEUE_ARRAY";
    private String OFFLINE_POST_DATA = "OFFLINE_POST_DATA";

    public LocalStorage(Activity activity) {
        sharedPref = activity.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void addToQueue(OfflinePost offlinePost) {
        ArrayList<OfflinePost> queue = getQueue();
        queue.add(offlinePost);
        Gson editGson = new Gson();
        String editJson = editGson.toJson(queue);
        SharedPreferences.Editor editor2 = sharedPref.edit();
        editor2.putString(OFFLINE_QUEUE_STR, editJson);
        editor2.commit();
    }

    public ArrayList<OfflinePost> getQueue() {
        Gson gson2 = new Gson();
        String json2 = sharedPref.getString(OFFLINE_QUEUE_STR, "");
        Type type = new TypeToken<List<OfflinePost>>() {}.getType();
        ArrayList<OfflinePost> offlineQueue = gson2.fromJson(json2, type);
        if (offlineQueue == null) {
            offlineQueue = new ArrayList<OfflinePost>();
        }
        return offlineQueue;
    }

    public void clearQueue() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(OFFLINE_QUEUE_STR);
        editor.commit();
        editor.apply();
    }

    public void saveOfflineData(List<Post> postData) {
        Gson gson = new Gson();
        String json = gson.toJson(postData);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(OFFLINE_POST_DATA, json);
        editor.commit();
    }

    public List<Post> getOfflineData() {
        Gson gson = new Gson();
        String json = sharedPref.getString(OFFLINE_POST_DATA, "");
        Type type = new TypeToken<List<Post>>() {}.getType();
        List<Post> offlineData = gson.fromJson(json, type);
        if (offlineData == null) {
            offlineData = new ArrayList<Post>();
        }
        return offlineData;
    }
}
