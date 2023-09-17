package com.app.memes.memesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.memes.memesocialmedia.adapters.PostListAdapter;
import com.app.memes.memesocialmedia.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton mainFab;
    private ListView updated_posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFab = findViewById(R.id.mainFab);
        updated_posts = (ListView) findViewById(R.id.item_list_data);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPost(v);
            }
        });

//        testing
        List<Post> postList = new ArrayList<>();
        postList.add(new Post("1", "2023", "I am batman", "https://firebasestorage.googleapis.com/v0/b/meme-social-media-4dce9.appspot.com/o/20230917_211757_351.jpg?alt=media&token=899f3c6a-45ac-4c11-b147-644333aa2296", 77));
        postList.add(new Post("1", "2023", "I am batman", "https://firebasestorage.googleapis.com/v0/b/meme-social-media-4dce9.appspot.com/o/20230917_211757_351.jpg?alt=media&token=899f3c6a-45ac-4c11-b147-644333aa2296", 77));
        postList.add(new Post("1", "2023", "I am batman", "https://firebasestorage.googleapis.com/v0/b/meme-social-media-4dce9.appspot.com/o/20230917_211757_351.jpg?alt=media&token=899f3c6a-45ac-4c11-b147-644333aa2296", 77));
        postList.add(new Post("1", "2023", "I am batman", "https://firebasestorage.googleapis.com/v0/b/meme-social-media-4dce9.appspot.com/o/20230917_211757_351.jpg?alt=media&token=899f3c6a-45ac-4c11-b147-644333aa2296", 77));
        postList.add(new Post("1", "2023", "I am batman", "https://firebasestorage.googleapis.com/v0/b/meme-social-media-4dce9.appspot.com/o/20230917_211757_351.jpg?alt=media&token=899f3c6a-45ac-4c11-b147-644333aa2296", 77));
        updated_posts.setAdapter(new PostListAdapter(getApplicationContext(), postList));
    }

    public void createNewPost(View v) {
        Intent intent = new Intent(MainActivity.this, CreatePost.class);
        startActivity(intent);
    }
}