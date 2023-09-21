package com.app.memes.memesocialmedia.services;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    LocalStorage(Activity activity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void save() {
        //
    }

    public void read() {
        //
    }
}
