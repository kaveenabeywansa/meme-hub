package com.app.memes.memesocialmedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.memes.memesocialmedia.interfaces.MemePostService;
import com.app.memes.memesocialmedia.models.Post;
import com.app.memes.memesocialmedia.services.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public void uploadImage(View v) {
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
        findViewById(R.id.please_wait).setVisibility(View.VISIBLE);

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byte bb[] = bytes.toByteArray();
        String file = Base64.encodeToString(bb, Base64.DEFAULT);
        myimage.setImageBitmap(thumbnail);

        uploadToFirebase(bb);
    }

    private void uploadToFirebase(byte[] bb) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String filename = timeStamp + "_" + (new Random().nextInt(900) + 100);
        StorageReference sr = mStorageRef.child(filename + ".jpg");
        sr.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(CreatePost.this, "Successfully Uplaod", Toast.LENGTH_SHORT).show();

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