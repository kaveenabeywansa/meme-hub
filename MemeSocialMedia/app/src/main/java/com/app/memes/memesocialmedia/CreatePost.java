package com.app.memes.memesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.memes.memesocialmedia.interfaces.MemePostService;
import com.app.memes.memesocialmedia.models.OfflinePost;
import com.app.memes.memesocialmedia.models.Post;
import com.app.memes.memesocialmedia.services.LocalStorage;
import com.app.memes.memesocialmedia.services.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePost extends AppCompatActivity {
    private StorageReference mStorageRef;
    private MemePostService memePostService;
    ImageView myimage;
    EditText postCaption;
    EditText postAuthor;

    Boolean isImgSelected = false;

    private String uploadFileLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        memePostService = RetrofitClient.getClient().create(MemePostService.class);

        myimage = findViewById(R.id.myimage);
        postCaption = findViewById(R.id.post_caption);
        postAuthor = findViewById(R.id.post_author);
    }

    public void selectImage(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byte bb[] = bytes.toByteArray();
        String file = Base64.encodeToString(bb, Base64.DEFAULT);
        myimage.setImageBitmap(thumbnail);
        isImgSelected = true;
    }

    public void goBack(View v) {
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void submitMeme(View v) {
        // simple validation - img or caption is available
        if (!(isImgSelected || postCaption.getText().toString().length() > 0)) {
            Toast.makeText(CreatePost.this, "Capture an image or enter caption to post!", Toast.LENGTH_SHORT).show();
            return;
        }

        // show loader
        findViewById(R.id.please_wait).setVisibility(View.VISIBLE);

        // get img to bytes
        myimage.setDrawingCacheEnabled(true);
        myimage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) myimage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgData = baos.toByteArray();

        if (!isNetworkAvailable()) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // save post offline
                            String memeId = UUID.randomUUID().toString();
                            String captionTxt = postCaption.getText().toString();
                            String author = postAuthor.getText().toString();
                            Post newPost = new Post(memeId, author, captionTxt, "", 0);
                            OfflinePost offlinePost = new OfflinePost();
                            offlinePost.setPost(newPost);
                            offlinePost.setPostImage(imgData);
                            new LocalStorage(CreatePost.this).addToQueue(offlinePost);
                            Toast.makeText(CreatePost.this,"Post added to queue!", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                    }
                    findViewById(R.id.please_wait).setVisibility(View.GONE);
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You seem to be offline! Do you want to add this to the offline queue?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
            return;
        }

        // upload image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String filename = "posts/" + timeStamp + "_" + (new Random().nextInt(900) + 100);
        StorageReference sr = mStorageRef.child(filename + ".jpg");
        sr.putBytes(imgData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get upload url
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("Link", uri.toString());
                        uploadFileLink = uri.toString();
                        // TODO: execute the remaining tasks
                        createPost(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreatePost.this,"Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPost(String imgUrl) {
        String memeId = UUID.randomUUID().toString();
        String captionTxt = postCaption.getText().toString();
        String author = postAuthor.getText().toString();

        // upload the new details to the server
        Post newPost = new Post(memeId, author, captionTxt, imgUrl, 0);
        Call call = memePostService.addMeme(newPost);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(CreatePost.this, "Meme Uploaded !", Toast.LENGTH_SHORT).show();
                // remove busy activity
                findViewById(R.id.please_wait).setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(CreatePost.this, "An error occurred !", Toast.LENGTH_SHORT).show();
                findViewById(R.id.please_wait).setVisibility(View.GONE);
                Log.i("Link",t.toString());
            }
        });
    }
}