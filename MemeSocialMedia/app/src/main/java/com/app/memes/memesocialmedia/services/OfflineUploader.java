package com.app.memes.memesocialmedia.services;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.memes.memesocialmedia.CreatePost;
import com.app.memes.memesocialmedia.R;
import com.app.memes.memesocialmedia.interfaces.MemePostService;
import com.app.memes.memesocialmedia.models.OfflinePost;
import com.app.memes.memesocialmedia.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfflineUploader {
    private Activity activity;
    private StorageReference mStorageRef;
    private MemePostService memePostService;

    public OfflineUploader(Activity act) {
        activity = act;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        memePostService = RetrofitClient.getClient().create(MemePostService.class);
    }
    public void uploadNow() {
        ArrayList<OfflinePost> queue = new LocalStorage(activity).getQueue();
        for (OfflinePost offlinePost : queue) {
            uploadPost(offlinePost);
        }
        Toast.makeText(activity, "Successfully Uploaded!", Toast.LENGTH_SHORT).show();
        new LocalStorage(activity).clearQueue();
    }

    private void uploadPost(OfflinePost offlinePost) {
        // upload image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String filename = "posts/" + timeStamp + "_" + (new Random().nextInt(900) + 100);
        StorageReference sr = mStorageRef.child(filename + ".jpg");
        sr.putBytes(offlinePost.getPostImage()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get upload url
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgUrl = uri.toString();
                        // upload the new details to the server
                        Post newPost = offlinePost.getPost();
                        newPost.setImageLink(imgUrl);
                        Call call = memePostService.addMeme(newPost);
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) {
//                                Toast.makeText(activity, "Meme Uploaded !", Toast.LENGTH_SHORT).show();
                                Log.i("Post", "Uploaded");
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                Toast.makeText(activity, "An error occurred !", Toast.LENGTH_SHORT).show();
                                Log.i("Link",t.toString());
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity,"Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
