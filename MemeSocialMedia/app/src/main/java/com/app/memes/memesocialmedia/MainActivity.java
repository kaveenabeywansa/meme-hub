package com.app.memes.memesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.memes.memesocialmedia.adapters.PostListAdapter;
import com.app.memes.memesocialmedia.interfaces.MemePostService;
import com.app.memes.memesocialmedia.models.OfflinePost;
import com.app.memes.memesocialmedia.models.Post;
import com.app.memes.memesocialmedia.models.PostsResponse;
import com.app.memes.memesocialmedia.services.LocalStorage;
import com.app.memes.memesocialmedia.services.OfflineUploader;
import com.app.memes.memesocialmedia.services.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton mainFab;
    FloatingActionButton mainFab2;
    FloatingActionButton mainFab3;
    private ListView updated_posts;
    private MemePostService memePostService;

    List<Post> memeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memePostService = RetrofitClient.getClient().create(MemePostService.class);
        mainFab = findViewById(R.id.mainFab);
        mainFab2 = findViewById(R.id.mainFab2);
        mainFab3 = findViewById(R.id.mainFab3);
        updated_posts = (ListView) findViewById(R.id.item_list_data);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPost(v);
            }
        });

        mainFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OfflineUploader(MainActivity.this).uploadNow();
                updateNetworkActivity();
                fetchAllMemes();
            }
        });

        mainFab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllMemes();
            }
        });

        //
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                updateNetworkActivity();
                ha.postDelayed(this, 2000);
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load posts
        fetchAllMemes();

        // update appbar
        updateNetworkActivity();
    }

    private void updateNetworkActivity() {
        if (isNetworkAvailable()) {
            findViewById(R.id.app_online).setVisibility(View.VISIBLE);
            findViewById(R.id.app_offline).setVisibility(View.GONE);
            findViewById(R.id.mainFab3).setVisibility(View.VISIBLE);

            // check if any offline queue data exists
            ArrayList<OfflinePost> offlineQueue = new LocalStorage(this).getQueue();
            Log.i("Offline", "Queue Size:" + offlineQueue.size());
            if (offlineQueue.size() > 0) {
                findViewById(R.id.mainFab2).setVisibility(View.VISIBLE);
                findViewById(R.id.mainFab3).setVisibility(View.GONE);
            } else {
                findViewById(R.id.mainFab2).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.app_online).setVisibility(View.GONE);
            findViewById(R.id.app_offline).setVisibility(View.VISIBLE);
            findViewById(R.id.mainFab2).setVisibility(View.GONE);
            findViewById(R.id.mainFab3).setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void createNewPost(View v) {
        Intent intent = new Intent(MainActivity.this, CreatePost.class);
        startActivity(intent);
    }

    private void fetchAllMemes() {
        if (!isNetworkAvailable()) {
            List<Post> offlineDataList = new LocalStorage(MainActivity.this).getOfflineData();
            if (offlineDataList != null && offlineDataList.size() > 0) {
                Log.i("Print Test", "Offline data count" + offlineDataList.size());
                updated_posts.setAdapter(new PostListAdapter(getApplicationContext(), offlineDataList, false));
            }

            return;
        }
        Log.i("Print Test", "Came here also!");
        memePostService.getAll().enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                // hide error and no data images
//                img_no_data.setVisibility(View.GONE);
//                img_error.setVisibility(View.GONE);

                PostsResponse postsResponse = (PostsResponse) response.body();
                memeList = postsResponse.getPostList();
                updated_posts.setAdapter(new PostListAdapter(getApplicationContext(), memeList, true));
                new LocalStorage(MainActivity.this).saveOfflineData(memeList);

                // no data found. display no data image
                if (memeList.size() < 1) {
                    Toast.makeText(getApplicationContext(), "No Data Found !", Toast.LENGTH_SHORT).show();
//                    img_no_data.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An internal error occurred !", Toast.LENGTH_LONG).show();
                Log.e("Error", t.getMessage());
                t.printStackTrace();
//                img_error.setVisibility(View.VISIBLE);
            }
        });
    }
}